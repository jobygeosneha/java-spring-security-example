package io.example.repository;

import io.example.domain.model.Book;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepo extends MongoRepository<Book, ObjectId> {

    @Cacheable("books")
    Optional<Book> findById(ObjectId objectId);

    @Cacheable("books")
    Iterable<Book> findAllById(Iterable<ObjectId> objectIds);

}
