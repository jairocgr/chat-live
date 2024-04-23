package io.chat.live.dto;

import io.chat.live.domain.RoomEventData;
import io.chat.live.domain.RoomEventType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class RoomEventDTO {
    private final int id;
    private final RoomEventType type;
    private final String author;
    private final String room;
    private final RoomEventData data;
    private final Instant createdAt;
}
