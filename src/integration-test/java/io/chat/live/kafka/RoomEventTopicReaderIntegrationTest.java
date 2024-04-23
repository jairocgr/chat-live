package io.chat.live.kafka;

import io.chat.live.BaseIntegrationTest;
import io.chat.live.domain.Message;
import io.chat.live.dto.RoomEventDTO;
import io.chat.live.event.NewMessage;
import io.chat.live.json.JsonSerializer;
import io.chat.live.repository.RoomEventRepository;
import io.chat.live.repository.RoomRepository;
import io.chat.live.service.NewMessagePublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.lang.Thread.sleep;
import static java.util.Collections.synchronizedList;
import static org.assertj.core.api.Assertions.assertThat;

class RoomEventTopicReaderIntegrationTest extends BaseIntegrationTest {

    /**
     * Time to wait in millis for the new message be saved and published in its topic
     */
    private static final long WAIT_FOR_NEW_MESSAGE = 400L;

    @Autowired
    ExecutorService executor;

    @Autowired
    KafkaProperties properties;

    @Autowired
    RoomRepository roomRepo;

    @Autowired
    RoomEventRepository events;

    @Autowired
    JsonSerializer serializer;

    @Autowired
    NewMessagePublisher publisher;

    List<Integer> eventsCreated = synchronizedList(new LinkedList<>());

    RoomEventTopicReader reader;

    @BeforeEach
    void setup() {
        reader = RoomEventTopicReader.builder()
            .repo(roomRepo)
            .properties(properties)
            .serializer(serializer)
            .build();
    }

    @AfterEach
    void cleanUp() {
        events.deleteAllById(eventsCreated);
        reader.stop();
    }

    @Test
    void testPrefetch() throws InterruptedException {
        var eventsReads = synchronizedList(new LinkedList<RoomEventDTO>());
        var newMessages = generateMessagesToBePublished();

        var rooms = List.of("legal", "general");

        for (var newMessage : newMessages) {
            publisher.publish(newMessage);
            // Wait for the events to be persisted and published so the reader can prefetch them
            sleep(WAIT_FOR_NEW_MESSAGE);
        }

        sleep(WAIT_FOR_NEW_MESSAGE);

        executor.execute(() -> reader.read(rooms, newMessages.size() / 2)
            .forEach(e -> {
                eventsReads.add(e);
                eventsCreated.add(e.getId());
            }));

        // Wait for the events to be prefetched by the reader
        sleep(WAIT_FOR_NEW_MESSAGE);

        assertMatch(newMessages, eventsReads);
    }

    @Test
    void testLiveReading() throws InterruptedException {
        var eventsReads = synchronizedList(new LinkedList<RoomEventDTO>());
        var newMessages = generateMessagesToBePublished();

        var rooms = List.of("legal", "general");

        executor.execute(() -> reader.read(rooms, 0)
            .forEach(e -> {
                eventsReads.add(e);
                eventsCreated.add(e.getId());
            }));

        for (var newMessage : newMessages) {
            publisher.publish(newMessage);
            // Wait for the events to be persisted and read by the event reader
            sleep(WAIT_FOR_NEW_MESSAGE);
        }

        assertMatch(newMessages, eventsReads);
    }

    private void assertMatch(List<NewMessage> newMessages, List<RoomEventDTO> eventsReads) {
        // Compare messages read to messages published
        assertThat(eventsReads.stream()
            .sorted((a, b) -> Integer.compare(a.getId(), b.getId()))
            .map(RoomEventDTO::getData)
            .toList())
            .usingRecursiveComparison()
            .isEqualTo(newMessages.stream()
                .map(NewMessage::getMessage)
                .toList());
    }

    private List<NewMessage> generateMessagesToBePublished() {
        var messages = new LinkedList<NewMessage>();

        var firstMessage = Message.builder()
            .time(Instant.now())
            .content("Itaque earum rerum hic tenetur a sapiente delectus")
            .build();

        messages.add(NewMessage.builder()
            .room("legal")
            .author("lucasg")
            .message(firstMessage)
            .build());

        var secondMessage = Message.builder()
            .time(Instant.now())
            .content("Lorem ipsum dolor sit amet, consectetur adipiscing elit")
            .build();

        messages.add(NewMessage.builder()
            .room("general")
            .author("jimmycarter")
            .message(secondMessage)
            .build());

        var thirdMessage = Message.builder()
            .time(Instant.now())
            .content("Quis autem vel eum iure reprehenderit")
            .build();

        messages.add(NewMessage.builder()
            .room("general")
            .author("tamara_spinazzola")
            .message(thirdMessage)
            .build());

        var forthMessage = Message.builder()
            .time(Instant.now())
            .content("Ut enim ad minima veniam, quis nostrum exercitationem ullam")
            .build();

        messages.add(NewMessage.builder()
            .room("legal")
            .author("tamara_spinazzola")
            .message(forthMessage)
            .build());

        return messages;
    }

}
