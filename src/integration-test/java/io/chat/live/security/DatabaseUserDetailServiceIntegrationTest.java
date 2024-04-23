package io.chat.live.security;

import io.chat.live.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseUserDetailServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    DatabaseUserDetailService service;

    @Autowired
    PasswordEncoder encoder;

    @Test
    void loadUserDetails() {
        var details = service.loadUserByUsername("tamara_spinazzola");
        assertEquals("tamara_spinazzola", details.getUsername());
        assertTrue(encoder.matches(DEFAULT_TEST_USER_PASSWORD, details.getPassword()));
        assertThat(details.getAuthorities())
            .allSatisfy(authority -> {
                assertEquals("ROLE_USER", authority.getAuthority());
            });
    }

    @Test
    void mustFailIfUnknownUser() {
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("jairocgr"));
    }
}
