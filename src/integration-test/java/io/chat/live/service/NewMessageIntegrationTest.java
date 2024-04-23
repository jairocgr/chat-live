package io.chat.live.service;

import io.chat.live.BaseIntegrationTest;
import io.chat.live.domain.Message;
import io.chat.live.dto.RoomEventDTO;
import io.chat.live.event.NewMessage;
import io.chat.live.helper.KafkaTestHelper;
import io.chat.live.json.JsonSerializer;
import io.chat.live.repository.RoomEventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.chat.live.domain.RoomEventType.MESSAGE;
import static io.chat.live.util.RoomUtils.roomTopic;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NewMessageIntegrationTest extends BaseIntegrationTest {

    /**
     * Time to wait in millis for the new message be saved and published in its topic
     */
    private static final long WAIT_FOR_NEW_MESSAGE = 400L;

    private static final String MESSAGE_TEXT = "Nam libero tempore, cum soluta nobis est eligendi";
    private static final String TEST_ROOM = "legal";
    private static final String TEST_USER = "lucasg";

    @Autowired
    NewMessagePublisher publisher;

    @Autowired
    RoomEventRepository events;

    @Autowired
    KafkaTestHelper kafka;

    @Autowired
    JsonSerializer serializer;

    private final Set<Integer> eventsCreated = new HashSet<>();

    @AfterEach
    public void cleanUp() {
        events.deleteAllById(eventsCreated);
    }

    @Test
    void mustCreateANewMessage() throws InterruptedException {

        var data = Message.builder()
            .time(Instant.now())
            .content(MESSAGE_TEXT)
            .build();

        var newMessage = NewMessage.builder()
            .room(TEST_ROOM)
            .author(TEST_USER)
            .message(data)
            .build();

        publisher.publish(newMessage);

        sleep(WAIT_FOR_NEW_MESSAGE);

        var resultingEvents = List.of(
            getLastEventOnDatabase(TEST_ROOM),
            getLastEventFromTopic(TEST_ROOM)
        );

        for (var event : resultingEvents) {
            assertEquals(MESSAGE, event.getType());
            assertEquals(TEST_USER, event.getAuthor());
            assertEquals(TEST_ROOM, event.getRoom());
            assertThat(event.getData())
                .usingRecursiveComparison()
                .isEqualTo(data);

            var id = event.getId();
            eventsCreated.add(id);
        }
    }

    private RoomEventDTO getLastEventFromTopic(String room) {
        return kafka.getLastRecordFrom(roomTopic(room), RoomEventDTO.class);
    }

    private RoomEventDTO getLastEventOnDatabase(String room) {
        return events.lastEventFrom(room).toDTO();
    }

}
