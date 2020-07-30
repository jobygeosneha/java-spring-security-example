package io.example.repository;

import io.example.domain.dto.SearchUsersRequest;
import io.example.domain.exception.NotFoundException;
import io.example.domain.model.User;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@Repository @CacheConfig(cacheNames = "users")
public interface UserRepo extends UserRepoCustom, MongoRepository<User, ObjectId> {

    @CacheEvict(allEntries = true)
    <S extends User> List<S> saveAll(Iterable<S> entities);

    @Caching(evict = {
            @CacheEvict(key = "#p0.id"),
            @CacheEvict(key = "#p0.username")
    })
    User save(User entity);

    @Cacheable
    Optional<User> findById(ObjectId objectId);

    @Cacheable
    default User getById(ObjectId id) {
        return findById(id).orElseThrow(() -> new NotFoundException(User.class, id));
    }

    @Cacheable
    Optional<User> findByUsername(String username);

}

interface UserRepoCustom {

    List<User> searchUsers(SearchUsersRequest request);

}

class UserRepoCustomImpl implements UserRepoCustom {

    private MongoTemplate mongoTemplate;

    UserRepoCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<User> searchUsers(SearchUsersRequest request) {
        List<AggregationOperation> operations = new ArrayList<>();

        List<Criteria> criterias = new ArrayList<>();
        if (!StringUtils.isEmpty(request.getUsername())) {
            criterias.add(Criteria.where("username").regex(String.format("^%s", request.getUsername()), "i"));
        }
        if (!StringUtils.isEmpty(request.getFullName())) {
            criterias.add(Criteria.where("fullName").regex(String.format("^%s", request.getFullName()), "i"));
        }
        if (!criterias.isEmpty()) {
            Criteria userCriteria = new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()]));
            operations.add(match(userCriteria));
        }

        operations.add(sort(Sort.Direction.DESC, "createdAt"));
        operations.add(skip((request.getPage() - 1) * request.getLimit()));
        operations.add(limit(request.getLimit()));

        TypedAggregation<User> aggregation = newAggregation(User.class, operations);
        AggregationResults<User> results = mongoTemplate.aggregate(aggregation, User.class);
        return results.getMappedResults();
    }
}
