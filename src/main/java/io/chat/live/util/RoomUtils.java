package io.chat.live.util;

public class RoomUtils {

    /**
     * Given a room handle, generate the matching topic name
     */
    public static String roomTopic(String roomHandle) {
        return "room-%s".formatted(roomHandle);
    }

    /**
     * Given a room handle, generate the matching topic name for new messages
     */
    public static String newMessageTopic(String roomHandle) {
        return "new-message-%s".formatted(roomHandle);
    }
}
