package io.example;

import io.example.domain.model.User;
import io.example.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

@Component
class DatabaseInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final List<String> usernames = Arrays.asList(
            "ada.lovelace@nix.com",
            "alan.turing@nix.com",
            "dennis.ritchie@nix.com"
    );
    private final String password = "Test12345_";

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        String encodedPassword = passwordEncoder.encode(password);

        for (String username : usernames) {
            if (userService.usernameExists(username)) {
                continue;
            }

            User user = new User(username, encodedPassword);
            userService.save(user);
        }
    }

}
