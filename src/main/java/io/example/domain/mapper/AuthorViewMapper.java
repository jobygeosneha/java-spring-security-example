package io.example.domain.mapper;

import io.example.domain.dto.AuthorView;
import io.example.domain.model.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorViewMapper {

    AuthorView toAuthorView(Author author);

}
