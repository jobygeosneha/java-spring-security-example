package io.example.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.example.api.data.AuthorTestDataFactory;
import io.example.api.data.BookTestDataFactory;
import io.example.domain.dto.AuthorView;
import io.example.domain.dto.BookView;
import io.example.domain.dto.ListResponse;
import io.example.domain.dto.SearchAuthorsRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Set;

import static io.example.util.JsonHelper.fromJson;
import static io.example.util.JsonHelper.toJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestAuthorSearchApi {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final AuthorTestDataFactory authorTestDataFactory;
    private final BookTestDataFactory bookTestDataFactory;

    @Autowired
    public TestAuthorSearchApi(MockMvc mockMvc,
                               ObjectMapper objectMapper,
                               AuthorTestDataFactory authorTestDataFactory,
                               BookTestDataFactory bookTestDataFactory) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.authorTestDataFactory = authorTestDataFactory;
        this.bookTestDataFactory = bookTestDataFactory;
    }

    @Test
    public void testSearch() throws Exception {
        AuthorView author1 = authorTestDataFactory.createAuthor("Author Search A Author", null, null, List.of("Author Search Genre A", "Author Search Genre B"));
        AuthorView author2 = authorTestDataFactory.createAuthor("Author Search B Author");
        AuthorView author3 = authorTestDataFactory.createAuthor("Author Search C Author");
        AuthorView author4 = authorTestDataFactory.createAuthor("Author Search D Author");
        AuthorView author5 = authorTestDataFactory.createAuthor("Author Search E Author");

        List<String> authorIds1 = List.of(author1.getId(), author2.getId(), author3.getId());
        List<String> authorIds2 = List.of(author4.getId(), author5.getId());

        BookView book1 = bookTestDataFactory.createBook(authorIds1, "Author Search A Book");
        BookView book2 = bookTestDataFactory.createBook(authorIds1, "Author Search B Book");
        BookView book3 = bookTestDataFactory.createBook(authorIds1, "Author Search C Book");
        BookView book4 = bookTestDataFactory.createBook(authorIds2, "Author Search D Book");
        BookView book5 = bookTestDataFactory.createBook(authorIds2, "Author Search E Book");

        testIdFilter(author1.getId());
        testFullNameFilter();
        testGenresFilter();
        testBookIdFilter(book1.getId());
        testBookTitleFilter();

        bookTestDataFactory.deleteBook(book1.getId());
        bookTestDataFactory.deleteBook(book2.getId());
        bookTestDataFactory.deleteBook(book3.getId());
        bookTestDataFactory.deleteBook(book4.getId());
        bookTestDataFactory.deleteBook(book5.getId());

        authorTestDataFactory.deleteAuthor(author1.getId());
        authorTestDataFactory.deleteAuthor(author2.getId());
        authorTestDataFactory.deleteAuthor(author3.getId());
        authorTestDataFactory.deleteAuthor(author4.getId());
        authorTestDataFactory.deleteAuthor(author5.getId());
    }

    private void testIdFilter(String id) throws Exception {
        SearchAuthorsRequest request;
        ListResponse<AuthorView> authorViewList;

        // Search request with book id equal
        request = new SearchAuthorsRequest();
        request.setId(id);
        authorViewList = execute("/api/author/search", request);
        assertEquals(1, authorViewList.getItems().size(), "Invalid search result!");
    }

    private void testFullNameFilter() throws Exception {
        SearchAuthorsRequest request;
        ListResponse<AuthorView> authorViewList;

        // Search request author full name contains
        request = new SearchAuthorsRequest();
        request.setFullName("Author Search A");
        authorViewList = execute("/api/author/search", request);
        assertEquals(1, authorViewList.getItems().size(), "Invalid search result!");

        // Search request author full name contains case insensitive
        request = new SearchAuthorsRequest();
        request.setFullName("Author Search b");
        authorViewList = execute("/api/author/search", request);
        assertEquals(1, authorViewList.getItems().size(), "Invalid search result!");
    }

    private void testGenresFilter() throws Exception {
        SearchAuthorsRequest request;
        ListResponse<AuthorView> authorViewList;

        // Search request genres all
        request = new SearchAuthorsRequest();
        request.setGenres(Set.of("Author Search Genre A", "Author Search Genre B"));
        authorViewList = execute("/api/author/search", request);
        assertEquals(1, authorViewList.getItems().size(), "Invalid search result!");

        // Search request genres mismatch
        request = new SearchAuthorsRequest();
        request.setGenres(Set.of("Author Search Genre A", "Author Search Genre C"));
        authorViewList = execute("/api/author/search", request);
        assertEquals(0, authorViewList.getItems().size(), "Invalid search result!");

        // Search request genres partial
        request = new SearchAuthorsRequest();
        request.setGenres(Set.of("Author Search Genre A"));
        authorViewList = execute("/api/author/search", request);
        assertEquals(1, authorViewList.getItems().size(), "Invalid search result!");
    }

    private void testBookIdFilter(String bookId) throws Exception {
        SearchAuthorsRequest request;
        ListResponse<AuthorView> authorViewList;

        // Search request with book id equal
        request = new SearchAuthorsRequest();
        request.setBookId(bookId);
        authorViewList = execute("/api/author/search", request);
        assertEquals(3, authorViewList.getItems().size(), "Invalid search result!");
    }

    private void testBookTitleFilter() throws Exception {
        SearchAuthorsRequest request;
        ListResponse<AuthorView> authorViewList;

        // Search request book title contains
        request = new SearchAuthorsRequest();
        request.setBookTitle("Author Search A");
        authorViewList = execute("/api/author/search", request);
        assertEquals(3, authorViewList.getItems().size(), "Invalid search result!");

        // Search request book title contains case insensitive
        request = new SearchAuthorsRequest();
        request.setBookTitle("Author Search c");
        authorViewList = execute("/api/author/search", request);
        assertEquals(3, authorViewList.getItems().size(), "Invalid search result!");
    }

    private ListResponse<AuthorView> execute(String url, SearchAuthorsRequest request) throws Exception {
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
