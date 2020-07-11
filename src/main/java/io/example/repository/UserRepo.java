package io.example.repository;

import io.example.domain.model.User;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository @CacheConfig(cacheNames = "users")
public interface UserRepo extends MongoRepository<User, ObjectId> {

    @Cacheable
    Optional<User> findById(ObjectId objectId);

    @Cacheable
    Optional<User> findByUsername(String username);

}
