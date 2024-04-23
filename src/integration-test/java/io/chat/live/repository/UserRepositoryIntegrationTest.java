package io.chat.live.repository;

import io.chat.live.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.chat.live.domain.UserRole.USER;
import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    UserRepository repo;

    @Test
    void checkIfUserExistsByLogin() {
        for (var login : TEST_USERS) {
            assertTrue(repo.existsByLogin(login));
        }
    }

    @Test
    void failIfUserDontExist() {
        assertFalse(repo.existsByLogin("jairocgr"));
    }

    @Test
    void mustRetrieveUser() {
        var user = repo.findByLogin("jimmycarter").orElseThrow();
        assertEquals("James Earl Carter", user.getName());
        assertEquals("jimmycarter", user.getLogin());
        assertEquals(USER, user.getRole());
    }

    @Test
    void failIfUserIsMissing() {
        var optional = repo.findByLogin("jimmycarter2");
        assertFalse(optional.isPresent());
    }

    @Test
    void testUserLimit() {
        var size = (int) repo.count();
        assertFalse(repo.userLimitReached(size + 1));
        assertTrue(repo.userLimitReached(size));
        assertTrue(repo.userLimitReached(size - 1));
    }
}
