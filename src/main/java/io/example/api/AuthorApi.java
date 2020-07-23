package io.example.api;

import io.example.domain.dto.AuthorView;
import io.example.domain.dto.BookView;
import io.example.domain.dto.EditAuthorRequest;
import io.example.domain.dto.ListResponse;
import io.example.domain.dto.SearchAuthorsRequest;
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

@Api(tags = "Author")
@RestController @RequestMapping(path = "api/author")
public class AuthorApi {

    private final AuthorService authorService;
    private final BookService bookService;

    public AuthorApi(AuthorService authorService,
                     BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    @PostMapping
    public AuthorView createAuthor(@RequestBody @Valid EditAuthorRequest request) {
        return authorService.create(request);
    }

    @PutMapping("{id}")
    public AuthorView editAuthor(@PathVariable String id, @RequestBody @Valid EditAuthorRequest request) {
        return authorService.update(new ObjectId(id), request);
    }

    @GetMapping("{id}")
    public AuthorView getAuthor(@PathVariable String id) {
        return authorService.getAuthor(new ObjectId(id));
    }

    @GetMapping("{id}/book")
    public ListResponse<BookView> getAuthorBooks(@PathVariable String id) {
        return new ListResponse<>(bookService.getAuthorBooks(new ObjectId(id)));
    }

    @PostMapping("search")
    public ListResponse<AuthorView> searchAuthors(@RequestBody @Valid SearchAuthorsRequest request) {
        return new ListResponse<>(authorService.searchAuthors(request));
    }

}
