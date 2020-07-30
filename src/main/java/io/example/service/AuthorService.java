package io.example.service;

import io.example.domain.dto.AuthorView;
import io.example.domain.dto.EditAuthorRequest;
import io.example.domain.dto.SearchAuthorsRequest;
import io.example.domain.mapper.AuthorEditMapper;
import io.example.domain.mapper.AuthorViewMapper;
import io.example.domain.model.Author;
import io.example.domain.model.Book;
import io.example.repository.AuthorRepo;
import io.example.repository.BookRepo;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthorService {

    private final AuthorRepo authorRepo;
    private final BookRepo bookRepo;
    private final AuthorEditMapper authorEditMapper;
    private final AuthorViewMapper authorViewMapper;

    public AuthorService(AuthorRepo authorRepo,
                         BookRepo bookRepo,
                         AuthorEditMapper authorEditMapper,
                         AuthorViewMapper authorViewMapper) {
        this.authorRepo = authorRepo;
        this.bookRepo = bookRepo;
        this.authorEditMapper = authorEditMapper;
        this.authorViewMapper = authorViewMapper;
    }

    @Transactional
    public AuthorView create(EditAuthorRequest request) {
        Author author = authorEditMapper.create(request);

        author = authorRepo.save(author);

        return authorViewMapper.toAuthorView(author);
    }

    @Transactional
    public AuthorView update(ObjectId id, EditAuthorRequest request) {
        Author author = authorRepo.getById(id);
        authorEditMapper.update(request, author);

        author = authorRepo.save(author);

        return authorViewMapper.toAuthorView(author);
    }

    @Transactional
    public AuthorView delete(ObjectId id) {
        Author author = authorRepo.getById(id);

        authorRepo.delete(author);
        bookRepo.deleteAll(bookRepo.findAllById(author.getBookIds()));

        return authorViewMapper.toAuthorView(author);
    }

    public AuthorView getAuthor(ObjectId id) {
        return authorViewMapper.toAuthorView(authorRepo.getById(id));
    }

    public List<AuthorView> getAuthors(Iterable<ObjectId> ids) {
        return authorViewMapper.toAuthorView(authorRepo.findAllById(ids));
    }

    public List<AuthorView> getBookAuthors(ObjectId bookId) {
        Book book = bookRepo.getById(bookId);
        return authorViewMapper.toAuthorView(authorRepo.findAllById(book.getAuthorIds()));
    }

    public List<AuthorView> searchAuthors(SearchAuthorsRequest request) {
        return authorViewMapper.toAuthorView(authorRepo.searchAuthors(request));
    }

}
