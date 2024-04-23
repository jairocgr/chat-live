package io.chat.live.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.lang.Math.max;

public class TopicReader {

    private static final Duration POOL_TIMEOUT = Duration.ofMillis(100L);

    private final KafkaConsumer<String, String> consumer;

    private ConsumerRecords<String, String> records;
    private Iterator<ConsumerRecord<String, String>> iterator;

    private boolean reading;

    public TopicReader(KafkaProperties properties) {
        var groupId = "%s-%s".formatted(TopicReader.class.getSimpleName(), UUID.randomUUID());
        var consumerProperties = properties.buildConsumerProperties(null);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumer = new KafkaConsumer<>(consumerProperties);
    }

    public Stream<ConsumerRecord<String, String>> read(List<String> topics, int messagesToPrefetch) {

        var partitions = topics.stream()
            .map(topic -> new TopicPartition(topic, 0))
            .toList();

        consumer.assign(partitions);
        consumer.seekToEnd(partitions);

        for (var partition : partitions) {
            var startPoint = consumer.position(partition) - messagesToPrefetch;
            var offset = max(startPoint, 0);
            consumer.seek(partition, offset);
        }

        records = consumer.poll(POOL_TIMEOUT);
        iterator = records.iterator();
        reading = true;
        return Stream.iterate(next(null), this::hasNext, this::next);
    }

    public boolean hasNext(ConsumerRecord<String, String> record) {
        return reading;
    }

    public ConsumerRecord<String, String> next(ConsumerRecord<String, String> record) {
        while (reading) {
            if (iterator.hasNext()) {
                return iterator.next();
            } else {
                records = consumer.poll(POOL_TIMEOUT);
                iterator = records.iterator();
            }
        }

        return null;
    }

    public void stop() {
        reading = false;
    }
}
