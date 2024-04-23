package io.chat.live.service;

import io.chat.live.BaseIntegrationTest;
import io.chat.live.domain.UserCredential;
import io.chat.live.exception.InvalidCredentialException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class AuthServiceIntegrationTest extends BaseIntegrationTest {

    private static final String DIRT = "3";

    @Autowired
    AuthService service;

    @Test
    void authUser() {
        var credential = UserCredential.builder()
            .login("tamara_spinazzola")
            .password(DEFAULT_TEST_USER_PASSWORD)
            .build();

        var user = service.auth(credential);

        assertEquals("tamara_spinazzola", user.getLogin());
        assertEquals(TEST_ROOMS.size(), user.getRooms().size());
        assertThat(user.getRooms()).allSatisfy(room -> {
           assertThat(room.getHandle()).isIn(TEST_ROOMS);
        });
    }

    @Test
    void failInvalidPassword() {
        var wrongPasswd = UserCredential.builder()
            .login("tamara_spinazzola")
            .password(DEFAULT_TEST_USER_PASSWORD + DIRT)
            .build();
        assertThrows(InvalidCredentialException.class, () -> service.auth(wrongPasswd));
    }

    @Test
    void failWithUnknownUser() {
        var wrongPasswd = UserCredential.builder()
            .login("tamara_spinazzola" + DIRT)
            .password(DEFAULT_TEST_USER_PASSWORD)
            .build();
        assertThrows(InvalidCredentialException.class, () -> service.auth(wrongPasswd));
    }

}
