package io.chat.live.domain;

import io.chat.live.fixture.RoomFixture;
import io.chat.live.fixture.UserFixture;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.chat.live.domain.UserRole.USER;
import static io.chat.live.fixture.UserFixture.USER_PASSWORD_HASH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void toUserDetails() {
        var details = new UserFixture()
            .kelly()
            .build()
            .toDetails();

        assertEquals("kelly3", details.getUsername());
        assertEquals(USER_PASSWORD_HASH, details.getPassword());
        assertTrue(details.isEnabled());
        assertTrue(details.isCredentialsNonExpired());
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertThat(details.getAuthorities())
            .allSatisfy(authority -> {
                assertEquals("ROLE_USER", authority.getAuthority());
            });
    }

    @Test
    void adminUserToDetails() {
        var details = new UserFixture()
            .adminUser()
            .build()
            .toDetails();

        assertThat(details.getAuthorities())
            .allSatisfy(authority -> {
                assertEquals("ROLE_ADMIN", authority.getAuthority());
            });
    }

    @Test
    void testDtoConversion() {
        var dto = new UserFixture()
            .kelly()
            .build()
            .toDTO();

        assertEquals(32, dto.getId());
        assertEquals("Kelly Elizabeth Taylor", dto.getName());
        assertEquals("kelly3", dto.getLogin());
        assertEquals(USER, dto.getRole());
    }

    @Test
    void testFullDtoConversion() {
        var general = new RoomFixture()
            .generalRoom()
            .build();

        var budget  = new RoomFixture()
            .budgetRoom()
            .build();

        var rooms = List.of(general, budget);

        var dto = new UserFixture()
            .kelly()
            .withRooms(rooms)
            .build()
            .toFullDTO();

        assertEquals(32, dto.getId());
        assertEquals("Kelly Elizabeth Taylor", dto.getName());
        assertEquals("kelly3", dto.getLogin());
        assertEquals(USER, dto.getRole());

        assertThat(dto.getRooms())
            .usingRecursiveComparison()
            .isEqualTo(List.of(general.toDTO(), budget.toDTO()));
    }

}
