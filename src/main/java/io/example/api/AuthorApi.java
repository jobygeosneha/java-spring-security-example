package io.example.api;

import io.example.domain.dto.AuthorView;
import io.example.domain.dto.EditAuthorRequest;

public interface AuthorApi {

    AuthorView createAuthor(EditAuthorRequest request);

    AuthorView editAuthor(String id, EditAuthorRequest request);

    AuthorView getAuthor(String id);

}
