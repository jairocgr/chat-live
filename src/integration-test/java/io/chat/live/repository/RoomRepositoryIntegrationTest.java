package io.chat.live.repository;

import io.chat.live.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class RoomRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    RoomRepository repo;

    @Test
    void mustLoadGeneralRoom() {
        var general = repo.getGeneralRoom();
        assertTrue(general.isGeneral());
        assertEquals("general", general.getHandle());
    }

    @Test
    void checkIfRoomExists() {
        assertTrue(repo.existsByHandle("general"));
        assertTrue(repo.existsByHandle("legal"));
    }

    @Test
    void failIfRoomDontExists() {
        assertFalse(repo.existsByHandle("unknown"));
    }

    @Test
    void mustRetrieveRoomByHandle() {
        var room = repo.findByHandle("legal")
            .orElseThrow();
        assertFalse(room.isGeneral());
        assertEquals("legal", room.getHandle());
    }

    @Test
    void mustNotReturnUnknownRoom() {
        var optional = repo.findByHandle("unknown");
        assertFalse(optional.isPresent());
    }

}
