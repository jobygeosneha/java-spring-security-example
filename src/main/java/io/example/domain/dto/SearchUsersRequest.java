package io.example.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
public class SearchUsersRequest extends PageRequest {

    private String username;
    private String fullName;

}
