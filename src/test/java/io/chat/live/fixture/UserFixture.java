package io.chat.live.fixture;

import io.chat.live.domain.Room;
import io.chat.live.domain.User;

import java.util.List;

import static io.chat.live.domain.UserRole.ADMIN;
import static io.chat.live.domain.UserRole.USER;
import static java.util.Collections.emptyList;

public class UserFixture implements Fixture<User> {

    /**
     * Password hash from raw password "bcrypt"
     */
    public final static String USER_PASSWORD_HASH = "$2y$10$cVBcEO2CPnvscniQkgsvGug9Fy3xYFBajeIHV4BqpCCWlN9WFuuYG";

    /**
     * Password hash from raw password "admin"
     */
    public final static String ADMIN_PASSWORD_HASH = "$2y$10$LsLj7OCV4BWpODozfeiOLOfMyIweHBCNP7MqL6XdDqUdDp.8VL5Fa";

    private User user;

    private User.UserBuilder baseUser() {
        return User.builder()
            .role(USER)
            // Password "bcrypt"
            .password(USER_PASSWORD_HASH)
            .rooms(emptyList());
    }

    public UserFixture adminUser() {
        user = baseUser()
            .id(1)
            .name("Joseph Thompson")
            .login("admin")
            .role(ADMIN)
            // Password "admin"
            .password(ADMIN_PASSWORD_HASH)
            .build();
        return this;
    }

    public UserFixture joseph() {
        user = baseUser()
            .id(16)
            .name("Joseph Thompson")
            .login("joseph_thompson")
            .build();
        return this;
    }

    public UserFixture kelly() {
        user = baseUser()
            .id(32)
            .name("Kelly Elizabeth Taylor")
            .login("kelly3")
            .build();
        return this;
    }

    public UserFixture withRooms(List<Room> rooms) {
        user = user.toBuilder()
            .rooms(rooms)
            .build();
        return this;
    }

    @Override
    public User build() {
        return user;
    }
}
