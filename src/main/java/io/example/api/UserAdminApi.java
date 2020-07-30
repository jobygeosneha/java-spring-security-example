package io.example.api;

import io.example.domain.dto.CreateUserRequest;
import io.example.domain.dto.SearchUsersRequest;
import io.example.domain.dto.UpdateUserRequest;
import io.example.domain.dto.UserView;
import io.example.service.UserService;
import io.swagger.annotations.Api;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "UserAdmin")
@RestController @RequestMapping(path = "api/admin/user")
public class UserAdminApi {

    private final UserService userService;

    public UserAdminApi(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserView createUser(@RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @PutMapping("{id}")
    public UserView updateUser(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        return userService.update(new ObjectId(id), request);
    }

    @DeleteMapping("{id}")
    public UserView deleteUser(@PathVariable String id) {
        return userService.delete(new ObjectId(id));
    }

    @GetMapping("{id}")
    public UserView getUser(@PathVariable String id) {
        return userService.getUser(new ObjectId(id));
    }

    @PostMapping
    public List<UserView> searchUsers(@RequestBody SearchUsersRequest request) {
        return userService.searchUsers(request);
    }

}
