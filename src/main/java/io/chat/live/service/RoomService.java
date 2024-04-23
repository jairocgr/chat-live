package io.chat.live.service;

import io.chat.live.domain.NewRoom;
import io.chat.live.domain.Room;
import io.chat.live.domain.RoomEvent;
import io.chat.live.domain.User;
import io.chat.live.dto.RoomDTO;
import io.chat.live.dto.RoomFullDTO;
import io.chat.live.exception.RoomFullException;
import io.chat.live.exception.RoomNotFoundException;
import io.chat.live.exception.UserNotFoundException;
import io.chat.live.kafka.RoomTopicCreator;
import io.chat.live.repository.RoomRepository;
import io.chat.live.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static io.chat.live.domain.RoomEventType.CREATE;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository rooms;
    private final UserRepository users;
    private final RoomEventService eventService;
    private final RoomTopicCreator creator;

    @Value("${app.room.max-allowed-members}")
    private final int maxMembersAllowed;

    /**
     * @param login Login of the user creating the new room
     * @param newRoom The new room
     * @return The newly created room data
     */
    @Transactional
    public RoomDTO create(String login, NewRoom newRoom) {
        var user = users.findByLogin(login).orElseThrow(() -> new UserNotFoundException(login));
        var room = newRoom.toRoom();
        rooms.save(room);
        creator.newTopicsFor(room);

        var creation = RoomEvent.builder()
            .author(user)
            .room(room)
            .type(CREATE)
            .build();

        eventService.save(creation);

        return room.toDTO();
    }

    /**
     * Given a user's login make it member of the given room.
     *
     * @throws RoomFullException when the room is unable of taking new members
     */
    @Transactional
    public void join(String login, String roomHandle) {
        var room = rooms.findByHandle(roomHandle).orElseThrow(() -> new RoomNotFoundException(roomHandle));
        var user = users.findByLogin(login).orElseThrow(() -> new UserNotFoundException(login));
        join(user, room);
    }

    @Transactional
    public void joinGeneralRoom(User user) {
        join(user, rooms.getGeneralRoom());
    }

    private void join(User user, Room room) {
        if (room.isFull(maxMembersAllowed)) {
            throw new RoomFullException(room, maxMembersAllowed);
        }
        var joining = user.join(room);
        eventService.save(joining);
    }

    public List<RoomFullDTO> all() {
        return rooms.findAll()
            .stream()
            .map(Room::toFullDTO)
            .toList();
    }
}
