package io.example;

import io.example.domain.dto.CreateUserRequest;
import io.example.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

@Component
class DatabaseInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final List<String> usernames = List.of(
            "ada.lovelace@nix.io",
            "alan.turing@nix.io",
            "dennis.ritchie@nix.io"
    );
    private final List<String> fullNames = List.of(
            "Ada Lovelace",
            "Alan Turing",
            "Dennis Ritchie"
    );
    private final String password = "Test12345_";

    private final UserService userService;

    public DatabaseInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        for (int i = 0; i < usernames.size(); ++i) {
            if (userService.usernameExists(usernames.get(i))) {
                continue;
            }

            CreateUserRequest request = new CreateUserRequest();
            request.setUsername(usernames.get(i));
            request.setFullName(fullNames.get(i));
            request.setPassword(password);
            request.setRePassword(password);

            userService.create(request);
        }
    }

}
