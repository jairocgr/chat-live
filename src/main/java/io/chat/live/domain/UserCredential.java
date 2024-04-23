package io.chat.live.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCredential {
    @NotBlank
    @Size(min = 2, max = 32)
    private final String login;
    @NotBlank
    @Size(min = 4, max = 64)
    private final String password;
}
