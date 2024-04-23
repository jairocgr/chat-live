package io.chat.live.outbox;

import com.github.javafaker.Faker;
import io.chat.live.BaseIntegrationTest;
import io.chat.live.json.JsonSerializer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.*;

import static io.chat.live.outbox.OutboxMessage.Status.ERROR;
import static io.chat.live.outbox.OutboxMessage.Status.PENDING;
import static io.chat.live.util.ListUtils.*;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(PER_CLASS)
class OutboxRepositoryIntegrationTest extends BaseIntegrationTest {

    private final static String TEST_TOPIC = "test-topic";
    private final static int GENERATED_TEST_EVENTS = 16;
    private final static int ERR_EVENT_DIVISOR = 3;
    private final static int TEST_ERROR_MESSAGES = GENERATED_TEST_EVENTS / ERR_EVENT_DIVISOR;
    private final static int TEST_PENDING_MESSAGES = GENERATED_TEST_EVENTS - TEST_ERROR_MESSAGES;

    @Autowired
    OutboxScheduler scheduler;

    @Autowired
    NamedParameterJdbcTemplate jdbc;

    @Autowired
    OutboxRepository repository;

    @Autowired
    JsonSerializer serializer;

    @BeforeEach
    void setUp() {
        // Pause the background outbox process
        scheduler.pause();
        clearOutbox();
        createFakeOutboxMessages();
    }

    @AfterAll
    void cleanUp() {
        clearOutbox();
        // Continue with the normal background outbox batch processing
        scheduler.carryOn();
    }

    @Test
    @Order(1)
    void testFetch() {
        var messages = repository.fetchPending(GENERATED_TEST_EVENTS);
        assertThat(messages).hasSize(TEST_PENDING_MESSAGES)
            .allSatisfy(message -> {
                assertThat(message.getEventId()).isBetween(1, GENERATED_TEST_EVENTS);
                assertEquals(TEST_TOPIC, message.getTopic());
                assertEquals(0, message.getAttempts());
                assertEquals(PENDING, message.getStatus());

                var event = serializer.fromJson(message.getData(), TestEventRecord.class);

                assertThat(event.id()).isBetween(1, GENERATED_TEST_EVENTS);
                assertThat(event.message()).hasSizeGreaterThanOrEqualTo(1);
            });
    }

    @Test
    @Order(2)
    void testIfNewMessagesIsProperlyCreated() {
        var event = new TestEventRecord(
            128, "Lorem ipsum dolor sit amet, consectetur adipiscing elit"
        );

        repository.save(TEST_TOPIC, event.id, event);

        var messages = repository.fetchPending(GENERATED_TEST_EVENTS);
        var lastMessages = lastItemFrom(messages);

        assertThat(messages).hasSizeGreaterThan(TEST_PENDING_MESSAGES);

        var deserializedEvent = serializer.fromJson(lastMessages.getData(), TestEventRecord.class);

        assertThat(deserializedEvent)
            .usingRecursiveComparison()
            .isEqualTo(event);
    }

    @Test
    @Order(3)
    void testIfIsIncreasingAttempt() {
        var messages = repository.fetchPending(1);
        var originalMessage = firstItemFrom(messages);

        repository.increaseAttempt(List.of(originalMessage));

        assertTrue(messagesExists(originalMessage.getId()));

        messages = repository.fetchPending(1);
        var changedMessage = firstItemFrom(messages);

        assertEquals(originalMessage.getId(), changedMessage.getId());
        assertEquals(originalMessage.getAttempts() + 1, changedMessage.getAttempts());
    }

    @Test
    @Order(3)
    void testIfIsMarkingAsError() {
        var messages = repository.fetchPending(GENERATED_TEST_EVENTS);
        var firstMessage = firstItemFrom(messages);

        repository.setFailedState(List.of(firstMessage));

        var newMessages = repository.fetchPending(GENERATED_TEST_EVENTS);

        assertThat(newMessages).hasSize(TEST_PENDING_MESSAGES - 1);

        assertEquals(ERROR, getMessageStatus(firstMessage.getId()));
    }

    @Test
    @Order(4)
    void testIfIsRemoving() {
        var messages = repository.fetchPending(GENERATED_TEST_EVENTS);
        var firstMessage = firstItemFrom(messages);
        var secondMessage = secondItemFrom(messages);

        repository.removeAll(List.of(firstMessage, secondMessage));

        var newMessages = repository.fetchPending(GENERATED_TEST_EVENTS);

        assertThat(newMessages).hasSize(TEST_PENDING_MESSAGES - 2);

        assertFalse(messagesExists(firstMessage.getId(), secondMessage.getId()));
    }

    private void clearOutbox() {
        jdbc.update("DELETE FROM live.outbox_message WHERE id > 0", emptyMap());
    }

    private void createFakeOutboxMessages() {
        var faker = new Faker();
        var lorem = faker.lorem();
        var messageCount = 0;
        while (messageCount < GENERATED_TEST_EVENTS) {
            var eventId = ++messageCount;
            var data = "{\"id\":%s,\"message\":\"%s\"}".formatted(eventId, lorem.sentence());
            var status = eventId % ERR_EVENT_DIVISOR == 0 ? ERROR : PENDING;
            var sql = """
                INSERT INTO live.outbox_message (status, topic, event_id, data)
                VALUES (:status::live.outbox_message_status, :topic, :event_id, :data);
            """;
            jdbc.update(sql, Map.of(
                "status", status.toString(),
                "topic", TEST_TOPIC,
                "event_id", eventId,
                "data", data
            ));
        }
    }

    private boolean messagesExists(int ...ids) {
        var idList = Arrays.stream(ids)
            .boxed()
            .toList();
        var sql = "SELECT count(*) FROM live.outbox_message WHERE id IN (:ids)";
        var args = new MapSqlParameterSource("ids", idList);
        var count = jdbc.queryForObject(sql, args, Integer.class);
        return count != null && count > 0;
    }

    private OutboxMessage.Status getMessageStatus(int id) {
        var sql = "SELECT status FROM live.outbox_message WHERE id = :id LIMIT 1";
        var args = new MapSqlParameterSource("id", id);
        var status = jdbc.queryForObject(sql, args, String.class);
        return OutboxMessage.Status.valueOf(status);
    }

    private record TestEventRecord(int id, String message) {}

}
