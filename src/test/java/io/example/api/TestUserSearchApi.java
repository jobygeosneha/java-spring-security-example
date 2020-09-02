package io.example.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.example.api.data.UserTestDataFactory;
import io.example.domain.dto.ListResponse;
import io.example.domain.dto.SearchUsersRequest;
import io.example.domain.dto.UserView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static io.example.util.JsonHelper.fromJson;
import static io.example.util.JsonHelper.toJson;
import static java.lang.System.currentTimeMillis;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithUserDetails("ada.lovelace@nix.io")
public class TestUserSearchApi {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserTestDataFactory userTestDataFactory;

    @Autowired
    public TestUserSearchApi(MockMvc mockMvc, ObjectMapper objectMapper, UserTestDataFactory userTestDataFactory) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userTestDataFactory = userTestDataFactory;
    }

    @Test
    public void testSearch() throws Exception {
        UserView user1 = userTestDataFactory.createUser(String.format("william.baker.%d@gmail.com", currentTimeMillis()), "William Baker");
        UserView user2 = userTestDataFactory.createUser(String.format("james.adams.%d@gmail.com", currentTimeMillis()), "James Adams");
        UserView user3 = userTestDataFactory.createUser(String.format("evelin.clarke.%d@nix.io", currentTimeMillis()), "Evelyn Clarke");
        UserView user4 = userTestDataFactory.createUser(String.format("ella.davidson.%d@nix.io", currentTimeMillis()), "Ella Davidson");
        UserView user5 = userTestDataFactory.createUser(String.format("evelin.bradley.%d@outlook.com", currentTimeMillis()), "Evelyn Bradley");

        testIdFilter(user1.getId());
        testUsernameFilter();
        testFullNameFilter();

        userTestDataFactory.deleteUser(user1.getId());
        userTestDataFactory.deleteUser(user2.getId());
        userTestDataFactory.deleteUser(user3.getId());
        userTestDataFactory.deleteUser(user4.getId());
        userTestDataFactory.deleteUser(user5.getId());
    }

    private void testIdFilter(String id) throws Exception {
        SearchUsersRequest request;
        ListResponse<UserView> userViewList;

        // Search request with book id equal
        request = new SearchUsersRequest();
        request.setId(id);
        userViewList = execute("/api/admin/user/search", request);
        assertEquals(1, userViewList.getItems().size(), "Invalid search result!");
    }

    private void testUsernameFilter() throws Exception {
        SearchUsersRequest request;
        ListResponse<UserView> userViewList;

        // Search request username starts with
        request = new SearchUsersRequest();
        request.setUsername("evelin");
        userViewList = execute("/api/admin/user/search", request);
        assertEquals(2, userViewList.getItems().size(), "Invalid search result!");

        // Search request username contains
        request = new SearchUsersRequest();
        request.setUsername("gmail");
        userViewList = execute("/api/admin/user/search", request);
        assertEquals(2, userViewList.getItems().size(), "Invalid search result!");

        // Search request username case insensitive
        request = new SearchUsersRequest();
        request.setUsername("William");
        userViewList = execute("/api/admin/user/search", request);
        assertEquals(1, userViewList.getItems().size(), "Invalid search result!");
    }

    private void testFullNameFilter() throws Exception {
        SearchUsersRequest request;
        ListResponse<UserView> userViewList;

        // Search request full name starts with
        request = new SearchUsersRequest();
        request.setUsername("William");
        userViewList = execute("/api/admin/user/search", request);
        assertEquals(1, userViewList.getItems().size(), "Invalid search result!");

        // Search request full name contains
        request = new SearchUsersRequest();
        request.setUsername("David");
        userViewList = execute("/api/admin/user/search", request);
        assertEquals(1, userViewList.getItems().size(), "Invalid search result!");

        // Search request full name case insensitive
        request = new SearchUsersRequest();
        request.setUsername("CLARKE");
        userViewList = execute("/api/admin/user/search", request);
        assertEquals(1, userViewList.getItems().size(), "Invalid search result!");
    }

    private ListResponse<UserView> execute(String url, SearchUsersRequest request) throws Exception {
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
