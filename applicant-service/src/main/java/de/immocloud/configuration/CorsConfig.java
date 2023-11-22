package de.immocloud.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/applicants")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "PATCH")
                .allowedHeaders("*");
    }
}

