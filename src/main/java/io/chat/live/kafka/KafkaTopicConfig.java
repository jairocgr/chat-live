package io.chat.live.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
@RequiredArgsConstructor
public class KafkaTopicConfig {

    private final KafkaProperties properties;

    @Value("${app.room.topic-replication}")
    private final int replicas;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        var configs = properties.buildAdminProperties(null);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic generalRoom() {
        return TopicBuilder.name("room-general")
            .partitions(1)
            .replicas(replicas)
            .config(TopicConfig.RETENTION_MS_CONFIG, "-1")
            .build();
    }

    @Bean
    public NewTopic generalRoomNewMessagesTopic() {
        return TopicBuilder.name("new-message-general")
            .partitions(1)
            .replicas(replicas)
            .build();
    }
}
