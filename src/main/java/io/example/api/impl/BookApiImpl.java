package io.example.api.impl;

import io.example.api.BookApi;
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

@Api(tags = "Book Api")
@RestController @RequestMapping(path = "api/book")
public class BookApiImpl implements BookApi {

    @Override @PostMapping
    public BookView createBook(@RequestBody EditBookRequest request) {
        return null;
    }

    @Override @PutMapping("{id}")
    public BookView editBook(@PathVariable String id, @RequestBody EditBookRequest request) {
        return null;
    }

    @Override @GetMapping("{id}")
    public BookView getBook(@PathVariable String id) {
        return null;
    }

}
