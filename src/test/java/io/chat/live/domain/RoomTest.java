package io.chat.live.domain;

import io.chat.live.fixture.RoomFixture;
import io.chat.live.fixture.UserListFixture;
import org.junit.jupiter.api.Test;

import static io.chat.live.domain.RoomType.GROUP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RoomTest {

    @Test
    void testDtoConversion() {
        var dto = new RoomFixture()
            .budgetRoom()
            .build()
            .toDTO();
        assertEquals(8, dto.getId());
        assertEquals("Budgeting 2025", dto.getName());
        assertEquals("budget-2025", dto.getHandle());
        assertEquals(GROUP, dto.getType());
    }

    @Test
    void testFullDtoConversion() {
        var users = new UserListFixture()
            .fixedList()
            .build();

        var usersDtoList = users.stream()
            .map(User::toDTO)
            .toList();

        var room = new RoomFixture()
            .budgetRoom()
            .withUsers(users)
            .build();

        var fullDto = room.toFullDTO();

        assertEquals(8, fullDto.getId());
        assertEquals("Budgeting 2025", fullDto.getName());
        assertEquals("budget-2025", fullDto.getHandle());
        assertEquals(GROUP, fullDto.getType());

        assertThat(fullDto.getUsers())
            .usingRecursiveComparison()
            .isEqualTo(usersDtoList);
    }

    @Test
    void mustVerifyRoomSizeLimit() {
        var users = new UserListFixture()
            .fixedList()
            .build();

        var room = new RoomFixture()
            .budgetRoom()
            .withUsers(users)
            .build();

        var maxMembersAllowed = users.size();

        assertTrue(room.isFull(maxMembersAllowed));
        assertTrue(room.isFull(maxMembersAllowed - 1));
        assertFalse(room.isFull(maxMembersAllowed + 1));
    }

    @Test
    void generalRoomMustNotHaveUserLimit() {
        var users = new UserListFixture()
            .fixedList()
            .build();

        var room = new RoomFixture()
            .generalRoom()
            .withUsers(users)
            .build();

        var maxMembersAllowed = users.size();

        assertFalse(room.isFull(maxMembersAllowed - 1));
        assertFalse(room.isFull(maxMembersAllowed));
        assertFalse(room.isFull(maxMembersAllowed + 1));
    }

    @Test
    void mustGenerateTopicNameCorrectly() {
        var room = new RoomFixture()
            .budgetRoom()
            .build();
        assertEquals("room-budget-2025", room.getTopicName());
    }

    @Test
    void newMessageTopicName() {
        var room = new RoomFixture()
            .budgetRoom()
            .build();
        assertEquals("new-message-budget-2025", room.getNewMessageTopicName());
    }
}
