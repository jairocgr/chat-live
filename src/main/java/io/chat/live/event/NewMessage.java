package io.chat.live.event;

import io.chat.live.domain.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class NewMessage {
    private final String room;
    private final String author;
    private final Message message;
}
