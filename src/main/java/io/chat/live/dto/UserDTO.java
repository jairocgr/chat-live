package io.chat.live.dto;

import io.chat.live.domain.UserRole;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDTO {
    private final int id;
    private final String name;
    private final String login;
    private final UserRole role;
}
