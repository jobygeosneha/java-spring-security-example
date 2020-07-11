package io.example.api;

import io.example.domain.dto.AuthorView;
import io.example.domain.dto.BookView;
import io.example.domain.dto.EditAuthorRequest;
import io.example.domain.dto.ListResponse;
import io.example.domain.mapper.AuthorEditMapper;
import io.example.domain.mapper.AuthorViewMapper;
import io.example.domain.mapper.BookViewMapper;
import io.example.domain.model.Author;
import io.example.domain.model.Book;
import io.example.service.AuthorService;
import io.example.service.BookService;
import io.swagger.annotations.Api;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Author")
@RestController @RequestMapping(path = "api/author")
public class AuthorApi {

    private final AuthorEditMapper authorEditMapper;
    private final AuthorViewMapper authorViewMapper;
    private final BookViewMapper bookViewMapper;
    private final AuthorService authorService;
    private final BookService bookService;

    public AuthorApi(AuthorEditMapper authorEditMapper,
                     AuthorViewMapper authorViewMapper,
                     BookViewMapper bookViewMapper,
                     AuthorService authorService,
                     BookService bookService) {
        this.authorEditMapper = authorEditMapper;
        this.authorViewMapper = authorViewMapper;
        this.bookViewMapper = bookViewMapper;
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @PostMapping
    public AuthorView createAuthor(@RequestBody EditAuthorRequest request) {
        Author author = authorEditMapper.create(request);

        author = authorService.save(author);

        return authorViewMapper.toAuthorView(author);
    }

    @PutMapping("{id}")
    public AuthorView editAuthor(@PathVariable String id, @RequestBody EditAuthorRequest request) {
        Author author = authorService.getAuthor(new ObjectId(id));
        authorEditMapper.update(request, author);

        author = authorService.save(author);

        return authorViewMapper.toAuthorView(author);
    }

    @GetMapping("{id}")
    public AuthorView getAuthor(@PathVariable String id) {
        Author author = authorService.getAuthor(new ObjectId(id));
        return authorViewMapper.toAuthorView(author);
    }

    @GetMapping("{id}/book")
    public ListResponse<BookView> getAuthorBooks(@PathVariable String id) {
        Author author = authorService.getAuthor(new ObjectId(id));
        List<Book> books = bookService.getBooks(author.getBookIds());
        return new ListResponse<>(bookViewMapper.toBookView(books));
    }

}
