package io.chat.live.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@RequiredArgsConstructor
public class TestTopicsConfiguration {

    @Value("${app.room.topic-replication}")
    private final int replicas;

    @Bean
    public NewTopic legalRoom() {
        return TopicBuilder.name("room-legal")
            .partitions(1)
            .replicas(replicas)
            .config(TopicConfig.RETENTION_MS_CONFIG, "-1")
            .build();
    }

    @Bean
    public NewTopic legalRoomNewMessagesTopic() {
        return TopicBuilder.name("new-message-legal")
            .partitions(1)
            .replicas(replicas)
            .build();
    }
}
