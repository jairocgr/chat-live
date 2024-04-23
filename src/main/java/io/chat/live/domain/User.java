package io.chat.live.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.chat.live.dto.UserDTO;
import io.chat.live.dto.UserFullDTO;
import io.chat.live.exception.CantJoinTwiceException;
import io.chat.live.security.EntityBasedUserDetail;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import static io.chat.live.domain.RoomEventType.JOIN;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(schema = "live")
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;
    private String name;
    private String login;

    @JsonIgnore
    private String password;

    @Enumerated(STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private UserRole role;

    @ManyToMany
    @JoinTable(
        schema = "live",
        name = "room_membership",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "room_id"))
    @Builder.Default
    private List<Room> rooms = new LinkedList<>();

    @Column(name = "created_at")
    @Builder.Default
    private Instant createdAt = Instant.now();

    public RoomEvent join(Room room) {
        if (rooms.contains(room)) {
            throw new CantJoinTwiceException();
        }
        rooms.add(room);
        return RoomEvent.builder()
            .type(JOIN)
            .room(room)
            .author(this)
            .build();
    }

    public UserDTO toDTO() {
        return UserDTO.builder()
            .id(id)
            .name(name)
            .login(login)
            .role(role)
            .build();
    }

    public UserFullDTO toFullDTO() {
        return UserFullDTO.builder()
            .id(id)
            .name(name)
            .login(login)
            .role(role)
            .rooms(rooms.stream()
                .map(Room::toDTO)
                .toList())
            .build();
    }

    public boolean joined(Room room) {
        return rooms.contains(room);
    }

    public UserDetails toDetails() {
        return new EntityBasedUserDetail(this);
    }
}



