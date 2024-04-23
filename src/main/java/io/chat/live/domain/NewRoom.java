package io.chat.live.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NewRoom {
    @NotBlank
    @Size(min = 2, max = 64)
    @Pattern(regexp = "^([a-z]+[a-z0-9\\-]*)$")
    private final String handle;
    @NotBlank
    @Size(min = 2, max = 128)
    private final String name;

    public Room toRoom() {
        return Room.builder()
            .handle(handle)
            .name(name)
            .build();
    }
}
