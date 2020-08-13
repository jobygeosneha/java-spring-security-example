package io.example.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.example.api.data.AuthorTestDataFactory;
import io.example.api.data.BookTestDataFactory;
import io.example.domain.dto.AuthorView;
import io.example.domain.dto.BookView;
import io.example.domain.dto.EditBookRequest;
import io.example.domain.dto.ListResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static io.example.util.JsonHelper.fromJson;
import static io.example.util.JsonHelper.toJson;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestBookApi extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthorTestDataFactory authorTestDataFactory;
    @Autowired
    private BookTestDataFactory bookTestDataFactory;

    @Test @WithMockUser
    public void testCreateSuccess() throws Exception {
        AuthorView authorView = authorTestDataFactory.createAuthor(mockMvc, "Test Author A");

        EditBookRequest goodRequest = new EditBookRequest();
        goodRequest.setAuthorIds(List.of(authorView.getId()));
        goodRequest.setTitle("Test Book A");

        MvcResult createResult = this.mockMvc
                .perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, goodRequest)))
                .andExpect(status().isOk())
                .andReturn();

        BookView bookView = fromJson(objectMapper, createResult.getResponse().getContentAsString(), BookView.class);
        assertNotNull(bookView.getId(), "Book id must not be null!");
        assertEquals(goodRequest.getTitle(), bookView.getTitle(), "Book title update isn't applied!");
    }

    @Test @WithMockUser
    public void testCreateFail() throws Exception {
        EditBookRequest badRequest = new EditBookRequest();

        this.mockMvc
                .perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, badRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Method argument validation failed")));
    }

    @Test @WithMockUser
    public void testEditSuccess() throws Exception {
        AuthorView authorView = authorTestDataFactory.createAuthor(mockMvc, "Test Author A");
        BookView bookView = bookTestDataFactory.createBook(mockMvc, List.of(authorView.getId()), "Test Book A");

        EditBookRequest updateRequest = new EditBookRequest();
        updateRequest.setTitle("Test Book B");
        updateRequest.setAbout("Cool Book");

        MvcResult updateResult = this.mockMvc
                .perform(put(String.format("/api/book/%s", bookView.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, updateRequest)))
                .andExpect(status().isOk())
                .andReturn();
        BookView newBookView = fromJson(objectMapper, updateResult.getResponse().getContentAsString(), BookView.class);

        assertEquals(updateRequest.getTitle(), newBookView.getTitle(), "Book title update isn't applied!");
        assertEquals(updateRequest.getAbout(), newBookView.getAbout(), "Book about update isn't applied!");
    }

    @Test @WithMockUser
    public void testEditFailBadRequest() throws Exception {
        AuthorView authorView = authorTestDataFactory.createAuthor(mockMvc, "Test Author A");
        BookView bookView = bookTestDataFactory.createBook(mockMvc, List.of(authorView.getId()), "Test Book A");

        EditBookRequest updateRequest = new EditBookRequest();

        this.mockMvc
                .perform(put(String.format("/api/book/%s", bookView.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Method argument validation failed")));
    }

    @Test @WithMockUser
    public void testEditFailNotFound() throws Exception {
        EditBookRequest updateRequest = new EditBookRequest();
        updateRequest.setTitle("Test Book A");

        this.mockMvc
                .perform(put(String.format("/api/book/%s", "5f07c259ffb98843e36a2aa9"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Entity Book with id 5f07c259ffb98843e36a2aa9 not found")));
    }

    @Test @WithMockUser
    public void testDeleteSuccess() throws Exception {
        AuthorView authorView = authorTestDataFactory.createAuthor(mockMvc, "Test Author A");
        BookView bookView = bookTestDataFactory.createBook(mockMvc, List.of(authorView.getId()), "Test Book A");

        this.mockMvc
                .perform(delete(String.format("/api/book/%s", bookView.getId())))
                .andExpect(status().isOk());

        this.mockMvc
                .perform(get(String.format("/api/book/%s", bookView.getId())))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    public void testDeleteFailNotFound() throws Exception {
        this.mockMvc
                .perform(delete(String.format("/api/book/%s", "5f07c259ffb98843e36a2aa9")))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Entity Book with id 5f07c259ffb98843e36a2aa9 not found")));
    }

    @Test @WithMockUser
    public void testGetSuccess() throws Exception {
        AuthorView authorView = authorTestDataFactory.createAuthor(mockMvc, "Test Author A");
        BookView bookView = bookTestDataFactory.createBook(mockMvc, List.of(authorView.getId()), "Test Book A");

        MvcResult getResult = this.mockMvc
                .perform(get(String.format("/api/book/%s", bookView.getId())))
                .andExpect(status().isOk())
                .andReturn();

        BookView getBookView = fromJson(objectMapper, getResult.getResponse().getContentAsString(), BookView.class);

        assertEquals(bookView.getId(), getBookView.getId(), "Book ids must be equal!");
    }

    @Test @WithMockUser
    public void testGetNotFound() throws Exception {
        this.mockMvc
                .perform(get(String.format("/api/book/%s", "5f07c259ffb98843e36a2aa9")))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Entity Book with id 5f07c259ffb98843e36a2aa9 not found")));
    }

    @Test @WithMockUser
    public void testGetBookAuthorsSuccess() throws Exception {
        AuthorView authorView1 = authorTestDataFactory.createAuthor(mockMvc, "Test Author A");
        AuthorView authorView2 = authorTestDataFactory.createAuthor(mockMvc, "Test Author B");
        BookView bookView = bookTestDataFactory.createBook(mockMvc, List.of(authorView1.getId(), authorView2.getId()), "Test Book A");

        MvcResult getBooksResult = this.mockMvc
                .perform(get(String.format("/api/book/%s/author", bookView.getId())))
                .andExpect(status().isOk())
                .andReturn();

        ListResponse<AuthorView> authorViewList = fromJson(objectMapper,
                getBooksResult.getResponse().getContentAsString(),
                new TypeReference<>() {});

        assertEquals(2, authorViewList.getItems().size(), "Book must have 2 authors");
        assertEquals(authorView1.getFullName(), authorViewList.getItems().get(0).getFullName(), "Author name mismatch!");
        assertEquals(authorView2.getFullName(), authorViewList.getItems().get(1).getFullName(), "Author name mismatch!");
    }

}
