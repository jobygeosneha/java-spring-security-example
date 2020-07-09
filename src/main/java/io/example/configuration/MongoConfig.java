package io.example.configuration;

import io.example.domain.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration @EnableMongoAuditing
public class MongoConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return user == null ? Optional.empty() : Optional.of(user.getId());
        };
    }

}
