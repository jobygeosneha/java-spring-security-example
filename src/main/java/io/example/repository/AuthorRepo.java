package io.example.repository;

import io.example.domain.model.Author;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository @CacheConfig(cacheNames = {"authors"})
public interface AuthorRepo extends MongoRepository<Author, ObjectId> {

    @Cacheable
    Optional<Author> findById(ObjectId objectId);

}
