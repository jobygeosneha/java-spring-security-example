package io.example.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.example.api.data.AuthorTestDataFactory;
import io.example.api.data.BookTestDataFactory;
import io.example.domain.dto.AuthorView;
import io.example.domain.dto.BookView;
import io.example.domain.dto.ListResponse;
import io.example.domain.dto.SearchBooksRequest;
import io.example.domain.dto.SearchUsersRequest;
import io.example.domain.dto.UserView;
import io.example.domain.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static io.example.util.JsonHelper.fromJson;
import static io.example.util.JsonHelper.toJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestBookSearchApi extends IntegrationTestBase {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final AuthorTestDataFactory authorTestDataFactory;
    private final BookTestDataFactory bookTestDataFactory;

    @Autowired
    public TestBookSearchApi(MockMvc mockMvc,
                             ObjectMapper objectMapper,
                             AuthorTestDataFactory authorTestDataFactory,
                             BookTestDataFactory bookTestDataFactory) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.authorTestDataFactory = authorTestDataFactory;
        this.bookTestDataFactory = bookTestDataFactory;
    }

    @Test @WithMockUser(roles = {Role.BOOK_ADMIN})
    public void testSearch() throws Exception {
        AuthorView author1 = authorTestDataFactory.createAuthor("Book Search A Author");
        AuthorView author2 = authorTestDataFactory.createAuthor("Book Search B Author");
        AuthorView author3 = authorTestDataFactory.createAuthor("Book Search C Author");

        List<String> authorIds1 = List.of(author1.getId(), author2.getId());
        List<String> authorIds2 = List.of(author3.getId());

        BookView book1 = bookTestDataFactory.createBook(authorIds1, "Book Search A Book", null, null, List.of("Book Search Genre A", "Book Search Genre B"));
        BookView book2 = bookTestDataFactory.createBook(authorIds1, "Book Search B Book", null, null, null, "978-1-56619-909-4");
        BookView book3 = bookTestDataFactory.createBook(authorIds1, "Book Search C Book", null, null, null, null, "1-56619-909-3");
        BookView book4 = bookTestDataFactory.createBook(authorIds1, "Book Search D Book", null, null, null, null, null, "Book Search A Publisher");
        BookView book5 = bookTestDataFactory.createBook(authorIds1, "Book Search E Book", null, null, null, null, null, null, LocalDate.of(1985, 7, 17));
        BookView book6 = bookTestDataFactory.createBook(authorIds2, "Book Search F Book");
        BookView book7 = bookTestDataFactory.createBook(authorIds2, "Book Search G Book");
        BookView book8 = bookTestDataFactory.createBook(authorIds2, "Book Search H Book");
        BookView book9 = bookTestDataFactory.createBook(authorIds2, "Book Search I Book");
        BookView book10 = bookTestDataFactory.createBook(authorIds2, "Book Search J Book");

        testIdFilter(book1.getId());
        testTitleFilter();
        testGenresFilter();
        testIsbn13Filter();
        testIsbn10Filter();
        testPublisherFilter();
        testPublishDateFilter();
        testAuthorIdFilter(author1.getId());
        testAuthorFullNameFilter();

        bookTestDataFactory.deleteBook(book1.getId());
        bookTestDataFactory.deleteBook(book2.getId());
        bookTestDataFactory.deleteBook(book3.getId());
        bookTestDataFactory.deleteBook(book4.getId());
        bookTestDataFactory.deleteBook(book5.getId());
        bookTestDataFactory.deleteBook(book6.getId());
        bookTestDataFactory.deleteBook(book7.getId());
        bookTestDataFactory.deleteBook(book8.getId());
        bookTestDataFactory.deleteBook(book9.getId());
        bookTestDataFactory.deleteBook(book10.getId());

        authorTestDataFactory.deleteAuthor(author1.getId());
        authorTestDataFactory.deleteAuthor(author2.getId());
        authorTestDataFactory.deleteAuthor(author3.getId());
    }

    private void testIdFilter(String id) throws Exception {
        SearchBooksRequest request;
        ListResponse<BookView> bookViewList;

        // Search request with book id equal
        request = new SearchBooksRequest();
        request.setId(id);
        bookViewList = execute("/api/book/search", request);
        assertEquals(1, bookViewList.getItems().size(), "Invalid search result!");
    }

    private void testTitleFilter() throws Exception {
        SearchBooksRequest request;
        ListResponse<BookView> bookViewList;

        // Search request book title contains
        request = new SearchBooksRequest();
        request.setTitle("Book Search G");
        bookViewList = execute("/api/book/search", request);
        assertEquals(1, bookViewList.getItems().size(), "Invalid search result!");

        // Search request book title contains case insensitive
        request = new SearchBooksRequest();
        request.setTitle("Book Search g");
        bookViewList = execute("/api/book/search", request);
        assertEquals(1, bookViewList.getItems().size(), "Invalid search result!");
    }

    private void testGenresFilter() throws Exception {
        SearchBooksRequest request;
        ListResponse<BookView> bookViewList;

        // Search request genres all
        request = new SearchBooksRequest();
        request.setGenres(Set.of("Book Search Genre A", "Book Search Genre B"));
        bookViewList = execute("/api/book/search", request);
        assertEquals(1, bookViewList.getItems().size(), "Invalid search result!");

        // Search request genres mismatch
        request = new SearchBooksRequest();
        request.setGenres(Set.of("Book Search Genre A", "Book Search Genre C"));
        bookViewList = execute("/api/book/search", request);
        assertEquals(0, bookViewList.getItems().size(), "Invalid search result!");

        // Search request genres partial
        request = new SearchBooksRequest();
        request.setGenres(Set.of("Book Search Genre A"));
        bookViewList = execute("/api/book/search", request);
        assertEquals(1, bookViewList.getItems().size(), "Invalid search result!");
    }

    private void testIsbn13Filter() throws Exception {
        SearchBooksRequest request;
        ListResponse<BookView> bookViewList;

        // Search request isbn13 equals
        request = new SearchBooksRequest();
        request.setIsbn13("978-1-56619-909-4");
        bookViewList = execute("/api/book/search", request);
        assertEquals(1, bookViewList.getItems().size(), "Invalid search result!");
    }

    private void testIsbn10Filter() throws Exception {
        SearchBooksRequest request;
        ListResponse<BookView> bookViewList;

        // Search request isbn10 equals
        request = new SearchBooksRequest();
        request.setIsbn10("1-56619-909-3");
        bookViewList = execute("/api/book/search", request);
        assertEquals(1, bookViewList.getItems().size(), "Invalid search result!");
    }

    private void testPublisherFilter() throws Exception {
        SearchBooksRequest request;
        ListResponse<BookView> bookViewList;

        // Search request book publisher contains
        request = new SearchBooksRequest();
        request.setPublisher("Book Search A Pub");
        bookViewList = execute("/api/book/search", request);
        assertEquals(1, bookViewList.getItems().size(), "Invalid search result!");

        // Search request book publisher contains case insensitive
        request = new SearchBooksRequest();
        request.setPublisher("Book Search a");
        bookViewList = execute("/api/book/search", request);
        assertEquals(1, bookViewList.getItems().size(), "Invalid search result!");
    }

    private void testPublishDateFilter() throws Exception {
        SearchBooksRequest request;
        ListResponse<BookView> bookViewList;

        // Search request publish date interval contains
        request = new SearchBooksRequest();
        request.setPublishDateStart(LocalDate.of(1985, 5, 1));
        request.setPublishDateEnd(LocalDate.of(1985, 9, 1));
        bookViewList = execute("/api/book/search", request);
        assertEquals(1, bookViewList.getItems().size(), "Invalid search result!");

        // Search request publish date interval not contains
        request = new SearchBooksRequest();
        request.setPublishDateStart(LocalDate.of(1985, 8, 1));
        request.setPublishDateEnd(LocalDate.of(1985, 9, 1));
        bookViewList = execute("/api/book/search", request);
        assertEquals(0, bookViewList.getItems().size(), "Invalid search result!");

        // Search request publish date interval start is inclusive
        request = new SearchBooksRequest();
        request.setPublishDateStart(LocalDate.of(1985, 7, 17));
        request.setPublishDateEnd(LocalDate.of(1985, 9, 1));
        bookViewList = execute("/api/book/search", request);
        assertEquals(1, bookViewList.getItems().size(), "Invalid search result!");

        // Search request publish date interval end is exclusive
        request = new SearchBooksRequest();
        request.setPublishDateStart(LocalDate.of(1985, 5, 1));
        request.setPublishDateEnd(LocalDate.of(1985, 7, 17));
        bookViewList = execute("/api/book/search", request);
        assertEquals(0, bookViewList.getItems().size(), "Invalid search result!");
    }

    private void testAuthorIdFilter(String authorId) throws Exception {
        SearchBooksRequest request;
        ListResponse<BookView> bookViewList;

        // Search request with book id equal
        request = new SearchBooksRequest();
        request.setAuthorId(authorId);
        bookViewList = execute("/api/book/search", request);
        assertEquals(5, bookViewList.getItems().size(), "Invalid search result!");
    }

    private void testAuthorFullNameFilter() throws Exception {
        SearchBooksRequest request;
        ListResponse<BookView> bookViewList;

        // Search request author full name contains
        request = new SearchBooksRequest();
        request.setAuthorFullName("Book Search A");
        bookViewList = execute("/api/book/search", request);
        assertEquals(5, bookViewList.getItems().size(), "Invalid search result!");

        // Search request author full name contains case insensitive
        request = new SearchBooksRequest();
        request.setAuthorFullName("Book Search c");
        bookViewList = execute("/api/book/search", request);
        assertEquals(5, bookViewList.getItems().size(), "Invalid search result!");
    }

    private ListResponse<BookView> execute(String url, SearchBooksRequest request) throws Exception {
        MvcResult result = this.mockMvc
                .perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, request)))
                .andExpect(status().isOk())
                .andReturn();

        return fromJson(objectMapper,
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
    }

}
