package io.example.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Set;

@Data @EqualsAndHashCode(callSuper = true)
public class SearchAuthorsRequest extends PageRequest {

    public SearchAuthorsRequest() {
        super(1, 10);
    }

    public SearchAuthorsRequest(int page, int limit) {
        super(page, limit);
    }

    private String id;

    private String creatorId;
    private LocalDateTime createdAtStart;
    private LocalDateTime createdAtEnd;

    private String fullName;
    private Set<String> genres;

    private String bookId;
    private String bookTitle;

}
