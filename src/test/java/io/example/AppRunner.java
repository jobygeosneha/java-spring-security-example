package io.example;

import io.example.domain.model.Book;
import io.example.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AppRunner {

    @Autowired private BookService bookService;

    @Test
    public void run() {
        List<Book> books = bookService.getBooksWithAuthors(0, 2);
        System.out.println(books);
    }

}
