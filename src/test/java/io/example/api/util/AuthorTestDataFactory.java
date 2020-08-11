package io.example.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.example.domain.dto.AuthorView;
import io.example.domain.dto.EditAuthorRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static io.example.util.JsonHelper.fromJson;
import static io.example.util.JsonHelper.toJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Service
public class AuthorTestDataFactory {

    @Autowired
    private ObjectMapper objectMapper;

    public AuthorView createAuthor(MockMvc mockMvc,
                                   String fullName,
                                   String about,
                                   String nationality,
                                   List<String> genres) throws Exception {
        EditAuthorRequest createRequest = new EditAuthorRequest();
        createRequest.setFullName(fullName);
        createRequest.setAbout(about);
        createRequest.setNationality(nationality);
        createRequest.setGenres(genres);

        MvcResult createResult = mockMvc
                .perform(post("/api/author")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, createRequest)))
                .andExpect(status().isOk())
                .andReturn();
        AuthorView authorView = fromJson(objectMapper, createResult.getResponse().getContentAsString(), AuthorView.class);

        assertNotNull(authorView.getId(), "Author id must not be null!");
        assertEquals(fullName, authorView.getFullName(), "Author name update isn't applied!");

        return authorView;
    }

    public AuthorView createAuthor(MockMvc mockMvc,
                                   String fullName,
                                   String about,
                                   String nationality) throws Exception {
        return createAuthor(mockMvc, fullName, about, nationality, null);
    }

    public AuthorView createAuthor(MockMvc mockMvc,
                                   String fullName,
                                   String about) throws Exception {
        return createAuthor(mockMvc, fullName, about, null, null);
    }

    public AuthorView createAuthor(MockMvc mockMvc,
                                   String fullName) throws Exception {
        return createAuthor(mockMvc, fullName, null, null, null);
    }

}
