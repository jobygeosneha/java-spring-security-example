package io.example.api;

import io.example.configuration.security.JwtTokenUtil;
import io.example.domain.dto.AuthRequest;
import io.example.domain.dto.UserView;
import io.example.domain.mapper.UserViewMapper;
import io.example.domain.model.User;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Authentication")
@RestController @RequestMapping(path = "api/auth")
public class AuthApi {

    private final Logger logger;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserViewMapper userViewMapper;

    public AuthApi(Logger logger,
                   AuthenticationManager authenticationManager,
                   JwtTokenUtil jwtTokenUtil,
                   UserViewMapper userViewMapper) {
        this.logger = logger;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userViewMapper = userViewMapper;
    }

    @PostMapping
    public ResponseEntity<UserView> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            User user = (User) authenticate.getPrincipal();

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, jwtTokenUtil.generateAccessToken(user))
                    .body(userViewMapper.toUserView(user));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
