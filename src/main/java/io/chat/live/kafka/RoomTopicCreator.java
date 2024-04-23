package io.chat.live.kafka;

import io.chat.live.domain.Room;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomTopicCreator {

    private final KafkaAdmin admin;

    @Value("${app.room.topic-replication}")
    private final int replicas;

    /**
     * Create the necessary Kafka topics for a Room, so it could receive and buffer
     * new messages and broadcast them to the clients.
     *
     * @param room the new Room being created
     */
    public void newTopicsFor(Room room) {
        createNewMessageTopic(room);
        createEventTopic(room);
    }

    private void createNewMessageTopic(Room room) {
        var name = room.getNewMessageTopicName();
        var newTopic = TopicBuilder.name(name)
            .partitions(1)
            .replicas(replicas)
            .build();
        admin.createOrModifyTopics(newTopic);
    }

    private void createEventTopic(Room room) {
        var name = room.getTopicName();
        var newTopic = TopicBuilder.name(name)
            .partitions(1)
            .replicas(replicas)
            .config(TopicConfig.RETENTION_MS_CONFIG, "-1")
            .build();
        admin.createOrModifyTopics(newTopic);
    }
}
