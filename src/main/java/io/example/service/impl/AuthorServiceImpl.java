package io.example.service.impl;

import io.example.domain.exception.NotFoundException;
import io.example.domain.model.Author;
import io.example.repository.AuthorRepo;
import io.example.service.AuthorService;
import org.springframework.stereotype.Service;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepo authorRepo;

    public AuthorServiceImpl(AuthorRepo authorRepo) {
        this.authorRepo = authorRepo;
    }

    @Override
    public Author save(Author author) {
        return authorRepo.save(author);
    }

    @Override
    public Author getAuthor(String id) {
        return authorRepo.findById(id).orElseThrow(() -> new NotFoundException(Author.class, id));
    }

}
