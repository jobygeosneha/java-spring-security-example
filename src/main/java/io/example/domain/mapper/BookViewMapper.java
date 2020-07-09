package io.example.domain.mapper;

import io.example.domain.dto.BookView;
import io.example.domain.model.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserViewMapper.class)
public interface BookViewMapper {

    BookView toBookView(Book book);

}
