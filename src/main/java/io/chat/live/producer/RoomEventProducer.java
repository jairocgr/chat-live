package io.chat.live.producer;

import io.chat.live.event.NewMessage;
import io.chat.live.json.JsonSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static io.chat.live.util.RoomUtils.newMessageTopic;

@Component
@RequiredArgsConstructor
public class RoomEventProducer {

    private final KafkaTemplate<String, Object> kafka;
    private final JsonSerializer serializer;

    public void send(NewMessage message) {
        var json = serializer.toJson(message);
        var room = message.getRoom();
        var topic = newMessageTopic(room);
        kafka.send(topic, room, json);
    }

}
