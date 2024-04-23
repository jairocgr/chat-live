package io.chat.live.domain;

import io.chat.live.dto.RoomEventDTO;
import io.chat.live.json.RoomEventDataType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.Instant;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "live", name = "room_event")
public class RoomEvent {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    @Enumerated(STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private RoomEventType type;

    @ManyToOne
    @JoinColumn(name = "author")
    private User author;

    @ManyToOne
    @JoinColumn(name = "room")
    private Room room;

    @Type(RoomEventDataType.class)
    private RoomEventData data;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    public RoomEventDTO toDTO() {
        return RoomEventDTO.builder()
            .id(id)
            .type(type)
            .author(author.getLogin())
            .room(room.getHandle())
            .data(data)
            .createdAt(createdAt)
            .build();
    }

    public boolean is(RoomEventType eventType) {
        return type == eventType;
    }
}
