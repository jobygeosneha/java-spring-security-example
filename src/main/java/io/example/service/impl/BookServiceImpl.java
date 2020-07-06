package io.example.service.impl;

import io.example.domain.model.Book;
import io.example.repository.BookRepo;
import io.example.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepo bookRepo;

    public BookServiceImpl(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    @Override
    public Book save(Book book) {
        return bookRepo.save(book);
    }

}
