package io.chat.live.service;

import io.chat.live.BaseIntegrationTest;
import io.chat.live.domain.NewUser;
import io.chat.live.dto.RoomEventDTO;
import io.chat.live.helper.KafkaTestHelper;
import io.chat.live.repository.RoomEventRepository;
import io.chat.live.repository.RoomRepository;
import io.chat.live.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.ExecutorService;

import static io.chat.live.domain.RoomEventType.JOIN;
import static io.chat.live.domain.UserRole.USER;
import static io.chat.live.util.RoomUtils.roomTopic;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserService service;

    @Autowired
    UserRepository repo;

    @Autowired
    RoomRepository rooms;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    KafkaTestHelper helper;

    @Autowired
    ExecutorService executor;

    @Autowired
    PlatformTransactionManager tm;

    @Autowired
    RoomEventRepository eventRepository;

    TransactionTemplate tt;

    @BeforeEach
    void setUp() {
        tt = new TransactionTemplate(tm);
    }

    @Test
    @Transactional
    void testUserCreation() throws InterruptedException {
        var newUser = NewUser.builder()
            .name("Jairo Rodrigues Filho")
            .login("jairo3")
            .password("C5876CBB878EB")
            .build();

        executor.execute(() -> {
            // Add user in a background thread
            tt.execute(s -> service.add(newUser));
        });

        // Wait for the joining event to be published by the outbox
        Thread.sleep(400L);

        var user = repo.findByLogin("jairo3")
            .orElseThrow();

        assertEquals("Jairo Rodrigues Filho", user.getName());
        assertEquals("jairo3", user.getLogin());
        assertEquals(USER, user.getRole());
        assertTrue(encoder.matches("C5876CBB878EB", user.getPassword()));

        var rooms = user.getRooms();
        assertEquals(1, rooms.size());

        var generalRoom = rooms.get(0);
        assertEquals("general", generalRoom.getHandle());
        assertTrue(generalRoom.isGeneral());

        var events = generalRoom.getEvents()
            .stream()
            .sorted((a, b) -> Integer.compare(a.getId(), b.getId()))
            .toList();
        var lastEvent = events.get(events.size() - 1);
        assertEquals(JOIN, lastEvent.getType());
        assertEquals("general", lastEvent.getRoom().getHandle());
        assertEquals("jairo3", lastEvent.getAuthor().getLogin());

        var topic = roomTopic("general");
        var joiningEvent = helper.getLastRecordFrom(topic, RoomEventDTO.class);
        assertEquals(JOIN, joiningEvent.getType());
        assertEquals("general", joiningEvent.getRoom());
        assertEquals("jairo3", joiningEvent.getAuthor());
    }
}
