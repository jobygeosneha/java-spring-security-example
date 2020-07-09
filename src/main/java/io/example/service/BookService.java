package io.example.service;

import io.example.domain.model.Book;
import io.example.repository.BookRepo;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepo bookRepo;

    public BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    public Book save(Book book) {
        return bookRepo.save(book);
    }

}
