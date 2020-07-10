package io.example.api;

import io.example.domain.dto.BookView;
import io.example.domain.dto.EditBookRequest;
import io.example.domain.mapper.BookEditMapper;
import io.example.domain.mapper.BookViewMapper;
import io.example.domain.model.Book;
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

@Api(tags = "Book")
@RestController @RequestMapping(path = "api/book")
public class BookApi {

    private final BookEditMapper bookEditMapper;
    private final BookViewMapper bookViewMapper;
    private final BookService bookService;

    public BookApi(BookEditMapper bookEditMapper,
                   BookViewMapper bookViewMapper,
                   BookService bookService) {
        this.bookEditMapper = bookEditMapper;
        this.bookViewMapper = bookViewMapper;
        this.bookService = bookService;
    }

    @PostMapping
    public BookView createBook(@RequestBody EditBookRequest request) {
        Book book = bookEditMapper.create(request);

        book = bookService.save(book);

        return bookViewMapper.toBookView(book);
    }

    @PutMapping("{id}")
    public BookView editBook(@PathVariable String id, @RequestBody EditBookRequest request) {
        Book book = bookService.getBook(new ObjectId(id));
        bookEditMapper.update(request, book);

        book = bookService.save(book);

        return bookViewMapper.toBookView(book);
    }

    @GetMapping("{id}")
    public BookView getBook(@PathVariable String id) {
        Book book = bookService.getBook(new ObjectId(id));
        return bookViewMapper.toBookView(book);
    }

}
