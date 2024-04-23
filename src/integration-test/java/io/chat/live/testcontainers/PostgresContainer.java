package io.chat.live.testcontainers;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainer extends PostgreSQLContainer<PostgresContainer> {

    private static final String IMAGE_VERSION = "postgres:16-alpine";
    private static final String DATABASE_NAME = "live";

    private static PostgresContainer container;

    private PostgresContainer() {
        super(IMAGE_VERSION);
    }

    public static PostgresContainer getInstance() {
        if (container == null) {
            container = new PostgresContainer()
                .withDatabaseName(DATABASE_NAME);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_TEST_URL", this.getJdbcUrl());
        System.setProperty("DB_TEST_USER", this.getUsername());
        System.setProperty("DB_TEST_PASSWORD", this.getPassword());
    }

    @Override
    public void stop() {
        // do nothing, JVM handles shut down
    }
}
