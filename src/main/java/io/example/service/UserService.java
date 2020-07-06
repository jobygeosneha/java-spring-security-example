package io.example.service;

import io.example.domain.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User save(User user);

    boolean usernameExists(String username);

}
