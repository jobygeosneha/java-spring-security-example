package io.example.service;

import io.example.domain.model.Author;

public interface AuthorService {

    Author save(Author author);

    Author getAuthor(String id);

}
