package io.example.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data @EqualsAndHashCode(callSuper = true)
public class SearchBooksRequest extends PageRequest {

    public SearchBooksRequest() {
        super(1, 10);
    }

    public SearchBooksRequest(int page, int limit) {
        super(page, limit);
    }

    private String id;

    private String creatorId;
    private LocalDateTime createdAtStart;
    private LocalDateTime createdAtEnd;

    private String title;
    private Set<String> genres;
    private String isbn13;
    private String isbn10;
    private String publisher;
    private LocalDate publishDateStart;
    private LocalDate publishDateEnd;

    private String authorId;
    private String authorFullName;

}
