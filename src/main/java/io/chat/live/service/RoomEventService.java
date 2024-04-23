package io.chat.live.service;

import io.chat.live.domain.RoomEvent;
import io.chat.live.event.NewMessage;
import io.chat.live.exception.RoomNotFoundException;
import io.chat.live.exception.UserNotFoundException;
import io.chat.live.outbox.OutboxService;
import io.chat.live.repository.RoomEventRepository;
import io.chat.live.repository.RoomRepository;
import io.chat.live.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static io.chat.live.domain.RoomEventType.MESSAGE;

@Service
@RequiredArgsConstructor
@Log4j2
public class RoomEventService {

    private final RoomEventRepository events;
    private final RoomRepository rooms;
    private final UserRepository users;
    private final OutboxService outbox;

    @Transactional
    public void process(NewMessage newMessage) {
        var handle = newMessage.getRoom();
        var login = newMessage.getAuthor();
        var message = newMessage.getMessage();
        var room = rooms.findByHandle(handle).orElseThrow(() -> new RoomNotFoundException(handle));
        var author = users.findByLogin(login).orElseThrow(() -> new UserNotFoundException(login));

        if (!author.joined(room)) {
            log.warn("New message but user {} is not a member of room {}",
                author.getLogin(),
                room.getHandle());
            return;
        }

        var event = RoomEvent.builder()
            .type(MESSAGE)
            .data(message)
            .author(author)
            .room(room)
            .build();

        save(event);
    }

    @Transactional
    public void save(RoomEvent event) {
        var room = event.getRoom();
        events.save(event);
        outbox.add(room, event);
    }
}
