package io.example;

import io.example.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AppRunner {

    @Autowired private BookService bookService;

    @Test
    public void run() {
    }

}
