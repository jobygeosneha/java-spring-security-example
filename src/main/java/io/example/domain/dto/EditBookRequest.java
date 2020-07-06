package io.example.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
public class EditBookRequest {

    private String id;

    @NotNull
    private String title;
    private String about;
    private String language;
    private List<String> genres;
    private String isbn13;
    private String isbn10;
    private String publisher;
    private LocalDate publishDate;
    private int hardcover;

    @NotNull
    private List<@NotNull String> authorIds;

}
