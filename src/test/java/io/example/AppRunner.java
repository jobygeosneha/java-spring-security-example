package io.example;

import io.example.domain.model.Author;
import io.example.repository.AuthorRepo;
import io.example.service.BookService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AppRunner {

    @Autowired private BookService bookService;
    @Autowired private AuthorRepo authorRepo;

    @Test
    public void run() {
        Author author = authorRepo.getById(new ObjectId("5f12d86ccc5b6963a5a55002"));
        System.out.println(author);
    }

}
