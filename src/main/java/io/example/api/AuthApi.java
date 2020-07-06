package io.example.api;

import io.example.domain.dto.AuthRequest;
import io.example.domain.dto.UserView;
import io.example.domain.model.User;
import org.springframework.http.ResponseEntity;

public interface AuthApi {

    ResponseEntity<UserView> login(AuthRequest authRequest);

}
