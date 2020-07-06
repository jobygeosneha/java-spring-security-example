package io.example.domain.mapper;

import io.example.domain.dto.UserView;
import io.example.domain.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserViewMapper {

    UserView toUserView(User user);

}
