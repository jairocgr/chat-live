package io.chat.live.service;

import io.chat.live.BaseIntegrationTest;
import io.chat.live.domain.RoomEvent;
import io.chat.live.repository.RoomRepository;
import io.chat.live.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import static io.chat.live.domain.RoomEventType.CREATE;
import static org.junit.jupiter.api.Assertions.*;

class RoomEventServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    RoomEventService service;

    @Autowired
    RoomRepository rooms;

    @Autowired
    UserRepository users;

    @Test
    void mustNotAddRoomCreationTwice() {
        var room = rooms.findByHandle("legal").orElseThrow();
        var user = users.findByLogin("admin").orElseThrow();
        var creation = RoomEvent.builder()
            .author(user)
            .room(room)
            .type(CREATE)
            .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            service.save(creation);
        });
    }

}
