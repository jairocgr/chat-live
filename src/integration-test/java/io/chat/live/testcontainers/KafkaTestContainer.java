package io.chat.live.testcontainers;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

public class KafkaTestContainer extends KafkaContainer {

    private static final String IMAGE_VERSION = "confluentinc/cp-kafka:7.6.0";
    private static KafkaContainer container;

    private KafkaTestContainer() {
        super(DockerImageName.parse(IMAGE_VERSION));
    }

    public static KafkaContainer getInstance() {
        if (container == null) {
            container = new KafkaTestContainer()
                .withEmbeddedZookeeper();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", getBootstrapServers());
    }

    @Override
    public void stop() {
        // do nothing, JVM handles shut down
    }
}
