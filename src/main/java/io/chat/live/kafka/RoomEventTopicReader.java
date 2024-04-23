package io.chat.live.kafka;

import io.chat.live.domain.Room;
import io.chat.live.dto.RoomEventDTO;
import io.chat.live.exception.RoomNotFoundException;
import io.chat.live.json.JsonSerializer;
import io.chat.live.repository.RoomRepository;
import lombok.Builder;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Builder
public class RoomEventTopicReader {

    private final KafkaProperties properties;
    private final JsonSerializer serializer;
    private final RoomRepository repo;

    private TopicReader reader;

    public Stream<RoomEventDTO> read(List<String> rooms, int prefetch) {
        reader = new TopicReader(properties);

        var topics = rooms.stream()
            .map(handle -> repo.findByHandle(handle)
                    .orElseThrow(() -> new RoomNotFoundException(handle)))
            .map(Room::getTopicName)
            .toList();

        return reader.read(topics, prefetch)
            .filter(Objects::nonNull)
            .map(ConsumerRecord::value)
            .map((string) -> serializer.fromJson(string, RoomEventDTO.class));
    }

    public synchronized void stop() {
        reader.stop();
    }

}
