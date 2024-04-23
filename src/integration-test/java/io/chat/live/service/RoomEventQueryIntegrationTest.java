package io.chat.live.service;

import io.chat.live.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

class RoomEventQueryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    RoomEventQuery query;

    @Test
    void mustListBeforeThreshold() {
        var list = query.before("general", 4);
        assertThat(list).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @Transactional
    void mustListAfterThreshold() {
        var list = query.after("general", 1)
            .toList();
        assertThat(list).hasSizeGreaterThanOrEqualTo(4);
    }
}
