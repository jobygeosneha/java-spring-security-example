package io.example.repository;

import io.example.domain.model.Author;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepo extends MongoRepository<Author, ObjectId> {

    @Cacheable("authors")
    Optional<Author> findById(ObjectId objectId);

    @Cacheable("authors")
    Iterable<Author> findAllById(Iterable<ObjectId> objectIds);

}
