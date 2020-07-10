package io.example.domain.mapper;

import io.example.domain.dto.UserView;
import io.example.domain.model.User;
import io.example.service.UserService;
import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

@Mapper(componentModel = "spring", uses = {UserService.class, ObjectIdMapper.class})
public abstract class UserViewMapper {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public abstract UserView toUserView(User user);

    public UserView toUserViewById(ObjectId id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return toUserView(userService.getUser(id));
    }

}
