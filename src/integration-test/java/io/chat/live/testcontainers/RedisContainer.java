package io.chat.live.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisContainer extends GenericContainer<RedisContainer> {

    private static RedisContainer instance;

    public static RedisContainer getInstance() {
        if (instance == null) {
            instance = new RedisContainer();
        }

        return instance;
    }

    private static final int REDIS_EXPOSED_PORT = 6379;

    public RedisContainer() {
        // The same settings defined in docker-compose.yml
        super(DockerImageName.parse("redis:7"));
        withExposedPorts(REDIS_EXPOSED_PORT);
    }

    @Override
    public void start() {
        super.start();

        // Assemble de redis host and port to be interpolated in the profile file
        // integration-test/resources/application-test.yml
        //
        var port = getMappedPort(REDIS_EXPOSED_PORT);
        var host = getHost();
        System.setProperty("REDIS_HOST", host);
        System.setProperty("REDIS_PORT", String.valueOf(port));
    }

    @Override
    public void stop() {
        // Do nothing, JVM handles shut down
    }

}
