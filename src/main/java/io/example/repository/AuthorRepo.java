package io.example.repository;

import io.example.domain.dto.SearchAuthorsRequest;
import io.example.domain.exception.NotFoundException;
import io.example.domain.model.Author;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

@Repository
public interface AuthorRepo extends MongoRepository<Author, ObjectId>, AuthorRepoCustom {

    default Author getById(ObjectId id) {
        return findById(id).orElseThrow(() -> new NotFoundException(Author.class, id));
    }

    List<Author> findAllById(Iterable<ObjectId> ids);

}

interface AuthorRepoCustom {

    List<Author> searchAuthors(SearchAuthorsRequest request);

}

class AuthorRepoCustomImpl implements AuthorRepoCustom {

    private final MongoTemplate mongoTemplate;

    AuthorRepoCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Author> searchAuthors(SearchAuthorsRequest request) {
        List<AggregationOperation> operations = new ArrayList<>();

        List<Criteria> criterias = new ArrayList<>();
        if (!StringUtils.isEmpty(request.getId())) {
            criterias.add(Criteria.where("id").is(new ObjectId(request.getId())));
        }
        if (!StringUtils.isEmpty(request.getCreatorId())) {
            criterias.add(Criteria.where("creatorId").is(new ObjectId(request.getCreatorId())));
        }
        if (request.getCreatedAtStart() != null) {
            criterias.add(Criteria.where("createdAt").gte(request.getCreatedAtStart()));
        }
        if (request.getCreatedAtEnd() != null) {
            criterias.add(Criteria.where("createdAt").lt(request.getCreatedAtEnd()));
        }
        if (!StringUtils.isEmpty(request.getFullName())) {
            criterias.add(Criteria.where("fullName").regex(String.format("^%s", request.getFullName()), "i"));
        }
        if (!CollectionUtils.isEmpty(request.getGenres())) {
            criterias.add(Criteria.where("genres").all(request.getGenres()));
        }
        if (!criterias.isEmpty()) {
            Criteria authorCriteria = new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()]));
            operations.add(match(authorCriteria));
        }

        criterias = new ArrayList<>();
        if (!StringUtils.isEmpty(request.getBookId())) {
            criterias.add(Criteria.where("book.id").is(new ObjectId(request.getBookId())));
        }
        if (!StringUtils.isEmpty(request.getBookTitle())) {
            criterias.add(Criteria.where("book.title").regex(String.format("^%s", request.getBookTitle()), "i"));
        }
        if (!criterias.isEmpty()) {
            Criteria bookCriteria = new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()]));
            operations.add(lookup("book", "bookIds", "_id", "book"));
            operations.add(unwind("book", false));
            operations.add(match(bookCriteria));
        }

        operations.add(sort(Sort.Direction.DESC, "createdAt"));

        TypedAggregation<Author> aggregation = newAggregation(Author.class, operations);
        AggregationResults<Author> results = mongoTemplate.aggregate(aggregation, Author.class);
        return results.getMappedResults();
    }
}