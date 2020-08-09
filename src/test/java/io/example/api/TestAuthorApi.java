package io.example.api;

import io.example.domain.dto.AuthorView;
import io.example.domain.dto.EditAuthorRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

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
public class TestAuthorApi extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test @WithMockUser
    public void testCreateSuccess() throws Exception {
        EditAuthorRequest goodRequest = new EditAuthorRequest();
        goodRequest.setFullName("Test Author A");

        MvcResult createResult = this.mockMvc
                .perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(goodRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AuthorView authorView = fromJson(createResult.getResponse().getContentAsString(), AuthorView.class);
        assertNotNull(authorView.getId(), "Author id must not be null!");
        assertEquals(authorView.getFullName(), "Test Author A", "Author name update isn't applied!");
    }

    @Test @WithMockUser
    public void testCreateFail() throws Exception {
        EditAuthorRequest badRequest = new EditAuthorRequest();

        this.mockMvc
                .perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(badRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Method argument validation failed")));
    }

    private AuthorView createAuthor() throws Exception {
        EditAuthorRequest createRequest = new EditAuthorRequest();
        createRequest.setFullName("Test Author A");

        MvcResult createResult = this.mockMvc
                .perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(createRequest)))
                .andExpect(status().isOk())
                .andReturn();
        AuthorView authorView = fromJson(createResult.getResponse().getContentAsString(), AuthorView.class);

        assertNotNull(authorView.getId(), "Author id must not be null!");
        assertEquals(authorView.getFullName(), "Test Author A", "Author name update isn't applied!");

        return authorView;
    }

    @Test @WithMockUser
    public void testEditSuccess() throws Exception {
        AuthorView newAuthorView = createAuthor();

        EditAuthorRequest updateRequest = new EditAuthorRequest();
        updateRequest.setFullName("Test Author B");
        updateRequest.setAbout("Cool author");

        MvcResult updateResult = this.mockMvc
                .perform(put(String.format("/api/author/%s", newAuthorView.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updateRequest)))
                .andExpect(status().isOk())
                .andReturn();
        AuthorView oldAuthorView = fromJson(updateResult.getResponse().getContentAsString(), AuthorView.class);

        assertEquals(oldAuthorView.getFullName(), "Test Author B", "Author name update isn't applied!");
        assertEquals(oldAuthorView.getAbout(), "Cool author", "Author name update isn't applied!");
    }

    @Test @WithMockUser
    public void testEditFailBadRequest() throws Exception {
        AuthorView newAuthorView = createAuthor();

        EditAuthorRequest updateRequest = new EditAuthorRequest();

        this.mockMvc
                .perform(put(String.format("/api/author/%s", newAuthorView.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Method argument validation failed")));
    }

    @Test @WithMockUser
    public void testEditFailNotFound() throws Exception {
        EditAuthorRequest updateRequest = new EditAuthorRequest();
        updateRequest.setFullName("Test Author B");

        this.mockMvc
                .perform(put(String.format("/api/author/%s", "5f07c259ffb98843e36a2aa9"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Entity Author with id 5f07c259ffb98843e36a2aa9 not found")));
    }

    @Test @WithMockUser
    public void testDeleteSuccess() throws Exception {
        AuthorView newAuthorView = createAuthor();

        this.mockMvc
                .perform(delete(String.format("/api/author/%s", newAuthorView.getId())))
                .andExpect(status().isOk());

        this.mockMvc
                .perform(get(String.format("/api/author/%s", newAuthorView.getId())))
                .andExpect(status().isNotFound());
    }

    @Test @WithMockUser
    public void testDeleteFailNotFound() throws Exception {
        this.mockMvc
                .perform(delete(String.format("/api/author/%s", "5f07c259ffb98843e36a2aa9")))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Entity Author with id 5f07c259ffb98843e36a2aa9 not found")));
    }

    @Test @WithMockUser
    public void testGetSuccess() throws Exception {
        AuthorView newAuthorView = createAuthor();

        MvcResult getResult = this.mockMvc
                .perform(get(String.format("/api/author/%s", newAuthorView.getId())))
                .andExpect(status().isOk())
                .andReturn();

        AuthorView authorView = fromJson(getResult.getResponse().getContentAsString(), AuthorView.class);

        assertEquals(newAuthorView.getId(), authorView.getId(), "Author ids must be equal!");
    }

    @Test @WithMockUser
    public void testGetNotFound() throws Exception {
        this.mockMvc
                .perform(get(String.format("/api/author/%s", "5f07c259ffb98843e36a2aa9")))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Entity Author with id 5f07c259ffb98843e36a2aa9 not found")));
    }

}
