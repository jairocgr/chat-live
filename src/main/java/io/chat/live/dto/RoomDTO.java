package io.chat.live.dto;

import io.chat.live.domain.RoomType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RoomDTO {
    private final int id;
    private final String name;
    private final String handle;
    private final RoomType type;
}
