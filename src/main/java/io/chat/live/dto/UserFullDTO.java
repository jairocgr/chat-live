package io.chat.live.dto;

import io.chat.live.domain.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserFullDTO {
    private final int id;
    private final String name;
    private final String login;
    private final UserRole role;
    private final List<RoomDTO> rooms;
}
