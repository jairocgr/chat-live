package io.chat.live.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import static io.chat.live.domain.UserRole.USER;

@Getter
@Builder
public class NewUser {
    @NotBlank
    @Size(min = 2, max = 128)
    private final String name;
    @NotBlank
    @Size(min = 2, max = 32)
    @Pattern(regexp = "^([a-z]+[a-z0-9\\-_]*)$")
    private final String login;
    @NotBlank
    @Size(min = 4, max = 64)
    private final String password;

    public User toUser(PasswordEncoder encoder) {
        return User.builder()
            .name(name)
            .login(login)
            .password(encoder.encode(password))
            .role(USER)
            .build();
    }
}
