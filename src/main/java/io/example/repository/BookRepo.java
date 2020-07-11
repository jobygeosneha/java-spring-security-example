package io.example.repository;

import io.example.domain.model.Book;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository @CacheConfig(cacheNames = "books")
public interface BookRepo extends MongoRepository<Book, ObjectId> {

    @Cacheable
    Optional<Book> findById(ObjectId objectId);

}
