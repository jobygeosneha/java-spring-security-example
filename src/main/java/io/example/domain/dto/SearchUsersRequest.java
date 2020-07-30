package io.example.domain.dto;

import lombok.Data;

@Data
public class SearchUsersRequest extends PageRequest {

    private String username;
    private String fullName;

}
