package io.example.api;

import io.example.domain.dto.BookView;
import io.example.domain.dto.EditBookRequest;
import io.swagger.annotations.Api;
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

    @PostMapping
    public BookView createBook(@RequestBody EditBookRequest request) {
        return null;
    }

    @PutMapping("{id}")
    public BookView editBook(@PathVariable String id, @RequestBody EditBookRequest request) {
        return null;
    }

    @GetMapping("{id}")
    public BookView getBook(@PathVariable String id) {
        return null;
    }

}
