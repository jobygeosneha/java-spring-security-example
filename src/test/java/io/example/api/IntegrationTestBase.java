package io.example.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.example.domain.dto.CreateUserRequest;
import io.example.domain.dto.UserView;
import io.example.service.UserService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

import static org.mockito.Mockito.when;

public class IntegrationTestBase {

    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuditorAware<ObjectId> auditorProvider;

    @BeforeEach
    public final void baseSetUp() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test@nix.io");
        request.setFullName("Test Tester");
        request.setPassword("Test12345_");
        request.setRePassword("Test12345_");
        UserView userView = userService.upsert(request);

        when(auditorProvider.getCurrentAuditor()).thenReturn(Optional.of(new ObjectId(userView.getId())));
    }

    @AfterEach
    public final void baseTearDown() {

    }

    protected <T> String toJson(T object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    protected <T> T fromJson(String payload, Class<T> clazz) throws JsonProcessingException {
        return objectMapper.readValue(payload, clazz);
    }

}
