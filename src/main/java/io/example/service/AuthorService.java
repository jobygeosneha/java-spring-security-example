package io.example.service;

import io.example.domain.exception.NotFoundException;
import io.example.domain.model.Author;
import io.example.repository.AuthorRepo;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

    private final AuthorRepo authorRepo;

    public AuthorService(AuthorRepo authorRepo) {
        this.authorRepo = authorRepo;
    }

    public Author save(Author author) {
        return authorRepo.save(author);
    }

    public Author getAuthor(String id) {
        return authorRepo.findById(id).orElseThrow(() -> new NotFoundException(Author.class, id));
    }

}
