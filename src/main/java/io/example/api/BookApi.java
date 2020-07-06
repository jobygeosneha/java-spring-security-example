package io.example.api;

import io.example.domain.dto.BookView;
import io.example.domain.dto.EditBookRequest;

public interface BookApi {

    BookView createBook(EditBookRequest request);

    BookView editBook(String id, EditBookRequest request);

    BookView getBook(String id);

}
