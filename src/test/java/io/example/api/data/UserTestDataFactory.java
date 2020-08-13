package io.example.api.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.example.domain.dto.CreateUserRequest;
import io.example.domain.dto.UserView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static io.example.util.JsonHelper.fromJson;
import static io.example.util.JsonHelper.toJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Service
public class UserTestDataFactory {

    @Autowired
    private ObjectMapper objectMapper;

    public UserView createUser(MockMvc mockMvc,
                               String username,
                               String fullName,
                               String password) throws Exception {
        CreateUserRequest createRequest = new CreateUserRequest();
        createRequest.setUsername(username);
        createRequest.setFullName(fullName);
        createRequest.setPassword(password);
        createRequest.setRePassword(password);

        MvcResult createResult = mockMvc
                .perform(post("/api/admin/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(objectMapper, createRequest)))
                .andExpect(status().isOk())
                .andReturn();
        UserView userView = fromJson(objectMapper, createResult.getResponse().getContentAsString(), UserView.class);

        assertNotNull(userView.getId(), "User id must not be null!");
        assertEquals(fullName, userView.getFullName(), "User name update isn't applied!");

        return userView;
    }

    public UserView createUser(MockMvc mockMvc,
                               String username,
                               String fullName) throws Exception {
        return createUser(mockMvc, username, fullName, "Test12345_");
    }



}
