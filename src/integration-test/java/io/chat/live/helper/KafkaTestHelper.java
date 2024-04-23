package io.chat.live.helper;

import io.chat.live.json.JsonSerializer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static java.lang.Math.max;

@Component
@RequiredArgsConstructor
public class KafkaTestHelper {

    private static final Duration POOL_TIMEOUT = Duration.ofMillis(100L);

    private final KafkaProperties properties;
    private final JsonSerializer serializer;

    public <T> T getLastRecordFrom(String topic, Class<T> clazz) {
        var record = getLastRecordsFrom(topic, 1)
            .iterator()
            .next();
        var json = record.value();
        return serializer.fromJson(json, clazz);
    }

    private ConsumerRecords<String, String> getLastRecordsFrom(String topic, int messagesToPrefetch) {
        var props = properties.buildConsumerProperties(null);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        try (var consumer = new KafkaConsumer<String, String>(props)) {
            var partitions = List.of(
                new TopicPartition(topic, 0)
            );

            consumer.assign(partitions);
            consumer.seekToEnd(partitions);

            for (var partition : partitions) {
                var startPoint = consumer.position(partition) - messagesToPrefetch;
                var offset = max(startPoint, 0);
                consumer.seek(partition, offset);
            }

            return consumer.poll(POOL_TIMEOUT);
        }
    }

    @SneakyThrows
    public boolean topicExists(String topic) {
        var props = properties.buildAdminProperties(null);
        try (AdminClient client = AdminClient.create(props)) {
            ListTopicsResult topics = client.listTopics();
            Set<String> topicList = topics.names().get();
            return topicList.contains(topic);
        }
    }
}
