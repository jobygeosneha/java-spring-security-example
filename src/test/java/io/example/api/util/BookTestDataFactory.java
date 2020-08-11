package io.example.api.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.example.domain.dto.AuthorView;
import io.example.domain.dto.BookView;
import io.example.domain.dto.EditAuthorRequest;
import io.example.domain.dto.EditBookRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

import static io.example.util.JsonHelper.fromJson;
import static io.example.util.JsonHelper.toJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Service
public class BookTestDataFactory {

    @Autowired
    private ObjectMapper objectMapper;

    public BookView createBook(MockMvc mockMvc,
                               List<String> authorIds,
                               String title,
                               String about,
                               String language,
                               List<String> genres,
                               String isbn13,
                               String isbn10,
                               String publisher,
                               LocalDate publishDate,
                               Integer hardcover) throws Exception {
        EditBookRequest createRequest = new EditBookRequest();
        createRequest.setAuthorIds(authorIds);
        createRequest.setTitle(title);
        createRequest.setAbout(about);
        createRequest.setLanguage(language);
        createRequest.setGenres(genres);
        createRequest.setIsbn13(isbn13);
        createRequest.setIsbn10(isbn10);
        createRequest.setPublisher(publisher);
        createRequest.setPublishDate(publishDate);
        createRequest.setHardcover(hardcover);

        MvcResult createResult = mockMvc
                .perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, createRequest)))
                .andExpect(status().isOk())
                .andReturn();
        BookView bookView = fromJson(objectMapper, createResult.getResponse().getContentAsString(), BookView.class);

        assertNotNull(bookView.getId(), "Book id must not be null!");
        assertEquals(title, bookView.getTitle(), "Book title update isn't applied!");

        return bookView;
    }

    public BookView createBook(MockMvc mockMvc,
                               List<String> authorIds,
                               String title,
                               String about,
                               String language,
                               List<String> genres,
                               String isbn13,
                               String isbn10,
                               String publisher,
                               LocalDate publishDate) throws Exception {
        return createBook(mockMvc, authorIds, title, about, language, genres, isbn13, isbn10, publisher, publishDate, null);
    }

    public BookView createBook(MockMvc mockMvc,
                               List<String> authorIds,
                               String title,
                               String about,
                               String language,
                               List<String> genres,
                               String isbn13,
                               String isbn10,
                               String publisher) throws Exception {
        return createBook(mockMvc, authorIds, title, about, language, genres, isbn13, isbn10, publisher, null, null);
    }

    public BookView createBook(MockMvc mockMvc,
                               List<String> authorIds,
                               String title,
                               String about,
                               String language,
                               List<String> genres,
                               String isbn13,
                               String isbn10) throws Exception {
        return createBook(mockMvc, authorIds, title, about, language, genres, isbn13, isbn10, null, null, null);
    }

    public BookView createBook(MockMvc mockMvc,
                               List<String> authorIds,
                               String title,
                               String about,
                               String language,
                               List<String> genres,
                               String isbn13) throws Exception {
        return createBook(mockMvc, authorIds, title, about, language, genres, isbn13, null, null, null, null);
    }

    public BookView createBook(MockMvc mockMvc,
                               List<String> authorIds,
                               String title,
                               String about,
                               String language,
                               List<String> genres) throws Exception {
        return createBook(mockMvc, authorIds, title, about, language, genres, null, null, null, null, null);
    }

    public BookView createBook(MockMvc mockMvc,
                               List<String> authorIds,
                               String title,
                               String about,
                               String language) throws Exception {
        return createBook(mockMvc, authorIds, title, about, language, null, null, null, null, null, null);
    }

    public BookView createBook(MockMvc mockMvc,
                               List<String> authorIds,
                               String title,
                               String about) throws Exception {
        return createBook(mockMvc, authorIds, title, about, null, null, null, null, null, null, null);
    }

    public BookView createBook(MockMvc mockMvc,
                               List<String> authorIds,
                               String title) throws Exception {
        return createBook(mockMvc, authorIds, title, null, null, null, null, null, null, null, null);
    }

}
