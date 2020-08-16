package io.example.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper = true)
public class SearchUsersRequest extends PageRequest {

    public SearchUsersRequest() {
        super(1, 10);
    }

    public SearchUsersRequest(int page, int limit) {
        super(page, limit);
    }

    private String id;
    private String username;
    private String fullName;

}
