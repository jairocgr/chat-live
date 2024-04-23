package io.chat.live.exception;

import io.chat.live.domain.Room;

public class RoomFullException extends TechnicalException {
    public RoomFullException(Room room, int limit) {
        super("Room %s is full (max members allowed %s)".formatted(room.getHandle(), limit));
    }
}
