package io.chat.live.fixture;

import io.chat.live.domain.Room;
import io.chat.live.domain.User;

import java.util.List;

import static io.chat.live.domain.RoomType.GROUP;
import static java.util.Collections.emptyList;

public class RoomFixture implements Fixture<Room> {

    private Room room;

    public RoomFixture generalRoom() {
        room = Room.builder()
            .id(1)
            .name("General")
            .handle("general")
            .general(true)
            .type(GROUP)
            .users(emptyList())
            .build();
        return this;
    }

    public RoomFixture budgetRoom() {
        room = Room.builder()
            .id(8)
            .name("Budgeting 2025")
            .handle("budget-2025")
            .type(GROUP)
            .users(emptyList())
            .build();
        return this;
    }

    public RoomFixture withUsers(List<User> users) {
        room = room.toBuilder()
            .users(users)
            .build();
        return this;
    }

    @Override
    public Room build() {
        return room;
    }
}
