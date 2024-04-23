package io.chat.live;

import io.chat.live.testcontainers.KafkaTestContainer;
import io.chat.live.testcontainers.PostgresContainer;
import io.chat.live.testcontainers.RedisContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BaseIntegrationTest {

    private static final PostgreSQLContainer<?> postgres = PostgresContainer.getInstance();
    private static final KafkaContainer kafka = KafkaTestContainer.getInstance();
    private static final RedisContainer redis = RedisContainer.getInstance();

    static {
        Startables.deepStart(postgres, kafka, redis).join();
    }

    protected static final List<String> TEST_ROOMS = List.of(
        "general",
        "legal"
    );

    protected static final List<String> TEST_USERS = List.of(
        "admin",
        "tamara_spinazzola",
        "jimmycarter",
        "lucasg"
    );

    protected static final String DEFAULT_TEST_USER_PASSWORD = "p4ssword";

}
