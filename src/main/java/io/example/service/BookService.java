package io.example.service;

import io.example.domain.exception.NotFoundException;
import io.example.domain.model.Author;
import io.example.domain.model.Book;
import io.example.repository.BookRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepo bookRepo;
    private final AuthorService authorService;

    public BookService(BookRepo bookRepo,
                       AuthorService authorService) {
        this.bookRepo = bookRepo;
        this.authorService = authorService;
    }

    @Transactional
    public Book save(Book book) {
        book = bookRepo.save(book);

        updateAuthors(book);

        return book;
    }

    @Transactional
    public List<Book> saveAll(List<Book> books) {
        books = bookRepo.saveAll(books);

        books.forEach(book -> updateAuthors(book));

        return books;
    }

    private void updateAuthors(Book book) {
        final String bookId = book.getId();
        List<Author> authors = authorService.getAuthors(book.getAuthorIds());
        authors.forEach(author -> author.getBookIds().add(bookId));
        authorService.saveAll(authors);
    }

    public Book getBook(String id) {
        return bookRepo.findById(id).orElseThrow(() -> new NotFoundException(Book.class, id));
    }

}
