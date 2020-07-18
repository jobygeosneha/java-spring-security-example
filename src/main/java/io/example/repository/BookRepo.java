package io.example.repository;

import io.example.domain.exception.NotFoundException;
import io.example.domain.model.Book;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepo extends MongoRepository<Book, ObjectId> {

    default Book getById(ObjectId id) {
        return findById(id).orElseThrow(() -> new NotFoundException(Book.class, id));
    }

    List<Book> findAllById(Iterable<ObjectId> ids);

}
