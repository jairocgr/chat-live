package io.chat.live.exception;

public class RoomNotFoundException extends RecordNotFoundException {

    public RoomNotFoundException(Object room) {
        super("Room \"%s\" not found".formatted(room));
    }
}
