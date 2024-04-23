package io.chat.live.domain;

import io.chat.live.dto.RoomDTO;
import io.chat.live.dto.RoomFullDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.Instant;
import java.util.List;

import static io.chat.live.domain.RoomType.GROUP;
import static io.chat.live.util.RoomUtils.newMessageTopic;
import static io.chat.live.util.RoomUtils.roomTopic;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(schema = "live")
public class Room {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @EqualsAndHashCode.Include
    private int id;

    private String name;
    private String handle;

    @Enumerated(STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Builder.Default
    private RoomType type = GROUP;

    @Builder.Default
    private boolean general = false;

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    @ManyToMany(mappedBy = "rooms")
    private List<User> users;

    @OneToMany(mappedBy = "room")
    private List<RoomEvent> events;

    public String getTopicName() {
        return roomTopic(handle);
    }

    public String getNewMessageTopicName() {
        return newMessageTopic(handle);
    }

    public RoomDTO toDTO() {
        return RoomDTO.builder()
            .id(id)
            .name(name)
            .handle(handle)
            .type(type)
            .build();
    }

    public RoomFullDTO toFullDTO() {
        return RoomFullDTO.builder()
            .id(id)
            .name(name)
            .handle(handle)
            .type(type)
            .users(users.stream()
                .map(User::toDTO)
                .toList())
            .build();
    }

    public boolean isFull(int maxMembersAllowed) {
        return !general && users.size() >= maxMembersAllowed;
    }
}
