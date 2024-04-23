package io.chat.live.outbox;

import io.chat.live.domain.Room;
import io.chat.live.domain.RoomEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;

@Service
@RequiredArgsConstructor
@Log4j2
public class OutboxService {

    private static final int MAX_ATTEMPT = 10;
    private static final int BATCH_SIZE = 1024;


    private final OutboxRepository messages;
    private final KafkaTemplate<String, String> kafka;

    /**
     * Add the event to the room's topic outbox for later send ({@link OutboxScheduler})
     */
    public void add(Room room, RoomEvent event) {
        var id = event.getId();
        var topic = room.getTopicName();
        var record = event.toDTO();
        messages.save(topic, id, record);
    }

    @Transactional
    public void sendPendingMessages() {
        var pendingMessages = messages.fetchPending(BATCH_SIZE);

        var successful = new LinkedList<OutboxMessage>();
        var failed = new LinkedList<OutboxMessage>();
        var attempted = new LinkedList<OutboxMessage>();

        for (var message : pendingMessages) {
            try {
                var topic = message.getTopic();
                var data = message.getData();
                kafka.send(topic, data);
                successful.add(message);
            } catch (Exception ex) {
                var msg = "Error sending outbox message %s".formatted(message.getId());
                log.error(msg, ex);
                if (message.getAttempts() > MAX_ATTEMPT) {
                    failed.add(message);
                } else {
                    attempted.add(message);
                }
            }
        }

        messages.removeAll(successful);
        messages.setFailedState(failed);
        messages.increaseAttempt(attempted);
        // TODO: for higher throughput, we could parallelize this task by topic
    }
}
