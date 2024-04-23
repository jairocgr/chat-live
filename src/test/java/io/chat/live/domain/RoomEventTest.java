package io.chat.live.domain;

import io.chat.live.fixture.RoomFixture;
import io.chat.live.fixture.UserFixture;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static io.chat.live.domain.RoomEventType.MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoomEventTest {

    @Test
    void toDtoConversion() {
        var room = new RoomFixture()
            .budgetRoom()
            .build();
        var author = new UserFixture()
            .kelly()
            .build();
        var message = Message.builder()
            .time(Instant.now())
            .content("At vero eos et accusamus et iusto odio dignissimos")
            .build();
        var event = RoomEvent.builder()
            .id(1)
            .type(MESSAGE)
            .room(room)
            .author(author)
            .data(message)
            .build();

        var dto = event.toDTO();

        assertEquals(1, dto.getId());
        assertEquals(MESSAGE, dto.getType());
        assertEquals(author.getLogin(), dto.getAuthor());
        assertEquals(room.getHandle(), dto.getRoom());

        assertThat(dto.getData())
            .usingRecursiveComparison()
            .isEqualTo(message);

        var now = Instant.now();
        var recentPass = now.minus(100L, ChronoUnit.MILLIS);
        assertTrue(dto.getCreatedAt().isAfter(recentPass));
        assertTrue(dto.getCreatedAt().isBefore(now));
    }

}
