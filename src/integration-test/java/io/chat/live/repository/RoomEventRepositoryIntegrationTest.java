package io.chat.live.repository;


import io.chat.live.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static java.lang.Integer.MAX_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
public class RoomEventRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    RoomEventRepository repo;

    @Test
    void listAfter() {
        var list = repo.after("legal", 0)
            .toList();
        assertThat(list).hasSizeGreaterThanOrEqualTo(4)
            .allSatisfy(event -> {
                assertThat(event.getRoom().getHandle()).isEqualTo("legal");
            });
    }

    @Test
    void listBefore() {
        var list = repo.before("legal", MAX_VALUE, 16);
        assertThat(list).hasSizeGreaterThanOrEqualTo(4)
            .allSatisfy(event -> {
                assertThat(event.getRoom().getHandle()).isEqualTo("legal");
            });
    }

    @Test
    void mustListBeforeThreshold() {
        var list = repo.before("general", 4, 16);
        assertThat(list).hasSizeGreaterThanOrEqualTo(2)
            .allSatisfy(event -> {
                assertThat(event.getRoom().getHandle()).isEqualTo("general");
            });
    }

    @Test
    void mustListAfterThreshold() {
        var list = repo.after("general", 1);
        assertThat(list).hasSizeGreaterThanOrEqualTo(4);
    }
}
