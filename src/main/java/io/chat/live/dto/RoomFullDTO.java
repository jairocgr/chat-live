package io.chat.live.dto;

import io.chat.live.domain.RoomType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoomFullDTO {
    private final int id;
    private final String name;
    private final String handle;
    private final RoomType type;
    private final List<UserDTO> users;
}
