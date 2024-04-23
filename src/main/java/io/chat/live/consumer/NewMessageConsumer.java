package io.chat.live.consumer;

import io.chat.live.event.NewMessage;
import io.chat.live.json.JsonSerializer;
import io.chat.live.service.RoomEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewMessageConsumer {

    private final RoomEventService service;
    private final JsonSerializer serializer;

    @KafkaListener(topicPattern = "new-message-.*")
    public void on(String data) {
        var message = serializer.fromJson(data, NewMessage.class);
        service.process(message);
    }

}
