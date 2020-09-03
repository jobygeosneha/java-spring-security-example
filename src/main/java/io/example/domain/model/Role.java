package io.example.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data @AllArgsConstructor @NoArgsConstructor
public class Role implements GrantedAuthority {

    public static final String ROLE_USER_ADMIN = "ROLE_USER_ADMIN";
    public static final String ROLE_AUTHOR_ADMIN = "ROLE_AUTHOR_ADMIN";
    public static final String ROLE_BOOK_ADMIN = "ROLE_BOOK_ADMIN";

    private String authority;

}
