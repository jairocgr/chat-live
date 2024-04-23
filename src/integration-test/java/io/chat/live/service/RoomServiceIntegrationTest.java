package io.chat.live.service;

import io.chat.live.BaseIntegrationTest;
import io.chat.live.domain.NewRoom;
import io.chat.live.domain.User;
import io.chat.live.dto.RoomEventDTO;
import io.chat.live.helper.KafkaTestHelper;
import io.chat.live.repository.RoomEventRepository;
import io.chat.live.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.ExecutorService;

import static io.chat.live.domain.RoomEventType.CREATE;
import static io.chat.live.domain.RoomEventType.JOIN;
import static io.chat.live.util.RoomUtils.newMessageTopic;
import static io.chat.live.util.RoomUtils.roomTopic;
import static org.junit.jupiter.api.Assertions.*;

class RoomServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    RoomService service;

    @Autowired
    ExecutorService executor;

    @Autowired
    KafkaTestHelper helper;

    @Autowired
    RoomRepository repo;

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
    void testRoomCreation() throws InterruptedException {
        var newRoom = NewRoom.builder()
            .name("Movies and Series")
            .handle("watch")
            .build();

        var expectedTopic = roomTopic("watch");
        var newMessageKafkaTopic = newMessageTopic("watch");

        executor.execute(() -> {
            tt.execute(s -> service.create("admin", newRoom));
        });

        // Wait for the room be created
        Thread.sleep(400L);

        var room = repo.findByHandle("watch").orElseThrow();
        assertEquals("Movies and Series", room.getName());

        // Check if the topics were created
        assertTrue(helper.topicExists(expectedTopic));
        assertTrue(helper.topicExists(newMessageKafkaTopic));

        var creation = helper.getLastRecordFrom(expectedTopic, RoomEventDTO.class);
        assertEquals(CREATE, creation.getType());
        assertEquals("watch", creation.getRoom());
        assertEquals("admin", creation.getAuthor());
    }

    @Test
    @Transactional
    void cantCreateRoomTwice() {
        var legal = NewRoom.builder()
            .name("Legal Dept")
            .handle("legal")
            .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            service.create("admin", legal);
        });
    }

    @Test
    @Transactional
    void testRoomJoining() throws InterruptedException {

        executor.execute(() -> {
            tt.execute(s -> {
                service.join("ally_boy", "legal");
                return null;
            });
        });

        // Wait for joining and the join event to be published
        Thread.sleep(500L);

        var room = repo.findByHandle("legal").orElseThrow();

        var members = room.getUsers()
            .stream()
            .map(User::getLogin)
            .toList();

        var joining = helper.getLastRecordFrom(roomTopic("legal"), RoomEventDTO.class);

        assertTrue(members.contains("ally_boy"));
        assertEquals(JOIN, joining.getType());
        assertEquals("legal", joining.getRoom());
        assertEquals("ally_boy", joining.getAuthor());
    }

}
