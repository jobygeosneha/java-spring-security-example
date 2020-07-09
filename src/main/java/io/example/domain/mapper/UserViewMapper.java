package io.example.domain.mapper;

import io.example.domain.dto.UserView;
import io.example.domain.model.User;
import io.example.service.UserService;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

@Mapper(componentModel = "spring", uses = UserService.class, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class UserViewMapper {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public abstract UserView toUserView(User user);

    public UserView toUserViewById(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return toUserView(userService.getUser(id));
    }

}
