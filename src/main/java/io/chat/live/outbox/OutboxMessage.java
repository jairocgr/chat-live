package io.chat.live.outbox;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class OutboxMessage {
    private final int id;
    private final Status status;
    private final String topic;
    private final int eventId;
    private final String data;
    private final int attempts;
    private final Instant createdAt;

    public enum Status { PENDING, ERROR }
}
