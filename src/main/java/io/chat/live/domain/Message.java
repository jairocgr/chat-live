package io.chat.live.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Getter
@Builder
@Jacksonized
public class Message implements RoomEventData {
    @NotBlank
    @Size(max = 1024)
    private final String content;

    @NotNull
    private Instant time;
}
