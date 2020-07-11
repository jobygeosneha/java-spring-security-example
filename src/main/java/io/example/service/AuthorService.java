package io.example.service;

import io.example.domain.exception.NotFoundException;
import io.example.domain.model.Author;
import io.example.domain.model.Book;
import io.example.repository.AuthorRepo;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepo authorRepo;
    private final BookService bookService;

    public AuthorService(AuthorRepo authorRepo, BookService bookService) {
        this.authorRepo = authorRepo;
        this.bookService = bookService;
    }

    public Author save(Author author) {
        return authorRepo.save(author);
    }

    public List<Author> saveAll(List<Author> authors) {
        return authorRepo.saveAll(authors);
    }

    public Author getAuthor(ObjectId id) {
        return authorRepo.findById(id).orElseThrow(() -> new NotFoundException(Author.class, id));
    }

    public List<Author> getAuthors(Iterable<ObjectId> ids) {
        List<Author> authors = new ArrayList<>();
        authorRepo.findAllById(ids).forEach(author -> authors.add(author));
        return authors;
    }

    public List<Book> getAuthorBooks(ObjectId id) {
        Author author = this.getAuthor(id);
        return bookService.getBooks(author.getBookIds());
    }

}
