package io.chat.live.service;

import io.chat.live.event.NewMessage;
import io.chat.live.exception.RoomNotFoundException;
import io.chat.live.exception.UserNotFoundException;
import io.chat.live.producer.RoomEventProducer;
import io.chat.live.repository.RoomRepository;
import io.chat.live.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewMessagePublisher {

    private final RoomRepository rooms;
    private final UserRepository users;
    private final RoomEventProducer producer;

    public void publish(NewMessage message) {
        var room = message.getRoom();
        var user = message.getAuthor();
        if (rooms.existsByHandle(room)) {
            if (users.existsByLogin(user)) {
                producer.send(message);
            } else {
                throw new UserNotFoundException(user);
            }
        } else {
            throw new RoomNotFoundException(room);
        }
    }
}
