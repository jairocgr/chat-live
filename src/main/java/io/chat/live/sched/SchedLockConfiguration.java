package io.chat.live.sched;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.jedis4.JedisLockProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
public class SchedLockConfiguration {

    @Value("${redis.host}")
    private String host;

    @Value("${redis.port}")
    private int port;

    @Value("${app.id}")
    private String appId;

    @Value("${spring.profiles.active:unknown}")
    private String activeProfile;

    @Bean
    public LockProvider lockProvider() {
        var pool = new JedisPool(host, port);
        var env = "%s-%s".formatted(appId, activeProfile);
        return new JedisLockProvider(pool, env);
    }
}
