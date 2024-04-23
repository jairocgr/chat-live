package io.chat.live.domain;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static io.chat.live.domain.UserRole.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewUserTest {

    private static PasswordEncoder encoder;

    @BeforeAll
    static void beforeAll() {
        encoder = new BCryptPasswordEncoder();
    }

    @Test
    void userEntityConversion() {
        var user = NewUser.builder()
            .name("Jairo Rodrigues Filho")
            .login("jairocgr")
            .password("jairo")
            .build()
            .toUser(encoder);

        assertEquals(0, user.getId());
        assertEquals("Jairo Rodrigues Filho", user.getName());
        assertEquals("jairocgr", user.getLogin());
        assertTrue(encoder.matches("jairo", user.getPassword()));
        assertEquals(USER, user.getRole());
        assertTrue(user.getRooms().isEmpty());

        var now = Instant.now();
        var recentPass = now.minus(100L, ChronoUnit.MILLIS);
        assertTrue(user.getCreatedAt().isAfter(recentPass));
        assertTrue(user.getCreatedAt().isBefore(now));
    }
}
