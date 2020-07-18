package io.example.repository;

import io.example.domain.exception.NotFoundException;
import io.example.domain.model.Author;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AuthorRepo extends MongoRepository<Author, ObjectId> {

    default Author getById(ObjectId id) {
        return findById(id).orElseThrow(() -> new NotFoundException(Author.class, id));
    }

    List<Author> findAllById(Iterable<ObjectId> ids);

}
