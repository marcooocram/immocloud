package de.immocloud.application;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "de.immocloud")
@EnableMongoRepositories(basePackages = "de.immocloud.persistence")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public OpenAPI openApiInformation() {
        Server localServer = new Server()
                .url("http://localhost:8102")
                .description("Localhost Server URL");

        Contact contact = new Contact()
                .email("molfem01@gmail.com")
                .name("Marco Molfese");

        Info info = new Info()
                .contact(contact)
                .title("applicants API (immocloud)")
                .version("V1.0.0");

        return new OpenAPI().info(info).addServersItem(localServer);
    }
}
