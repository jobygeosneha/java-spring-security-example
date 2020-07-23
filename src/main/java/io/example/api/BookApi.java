package io.example.api;

import io.example.domain.dto.AuthorView;
import io.example.domain.dto.BookView;
import io.example.domain.dto.EditBookRequest;
import io.example.domain.dto.ListResponse;
import io.example.domain.dto.SearchBooksRequest;
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

import javax.validation.Valid;

@Api(tags = "Book")
@RestController @RequestMapping(path = "api/book")
public class BookApi {

    private final BookService bookService;
    private final AuthorService authorService;

    public BookApi(BookService bookService,
                   AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    @PostMapping
    public BookView createBook(@RequestBody @Valid EditBookRequest request) {
        return bookService.create(request);
    }

    @PutMapping("{id}")
    public BookView editBook(@PathVariable String id, @RequestBody @Valid EditBookRequest request) {
        return bookService.update(new ObjectId(id), request);
    }

    @GetMapping("{id}")
    public BookView getBook(@PathVariable String id) {
        return bookService.getBook(new ObjectId(id));
    }

    @GetMapping("{id}/author")
    public ListResponse<AuthorView> getBookAuthors(@PathVariable String id) {
        return new ListResponse<>(authorService.getBookAuthors(new ObjectId(id)));
    }

    @PostMapping("search")
    public ListResponse<BookView> searchBooks(@RequestBody @Valid SearchBooksRequest request) {
        return new ListResponse<>(bookService.searchBooks(request));
    }

}
