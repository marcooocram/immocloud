package de.immocloud.utils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.testcontainers.containers.GenericContainer;

@Configuration
public class TestMongoConfiguration {

    @Bean
    public GenericContainer<?> mongoContainer() {
        GenericContainer<?> container = new GenericContainer<>("mongo:latest")
                .withExposedPorts(27017);

        container.start();

        return container;
    }

    @Bean
    public MongoTemplate mongoTemplate(GenericContainer<?> mongoContainer) {
        String connectionUrl = "mongodb://" + mongoContainer.getContainerIpAddress() + ":" +
                mongoContainer.getMappedPort(27017) + "/applicants-mongodb";

        connectionUrl += "?uuidRepresentation=standard";

        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(connectionUrl));
    }
}
