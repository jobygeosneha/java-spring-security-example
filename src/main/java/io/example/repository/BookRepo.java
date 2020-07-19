package io.example.repository;

import io.example.domain.dto.SearchBooksRequest;
import io.example.domain.exception.NotFoundException;
import io.example.domain.model.Book;
import org.bson.types.ObjectId;
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

import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

@Repository
public interface BookRepo extends MongoRepository<Book, ObjectId>, BookRepoCustom {

    default Book getById(ObjectId id) {
        return findById(id).orElseThrow(() -> new NotFoundException(Book.class, id));
    }

    List<Book> findAllById(Iterable<ObjectId> ids);

}

interface BookRepoCustom {

    List<Book> searchBooks(SearchBooksRequest request);

}

class BookRepoCustomImpl implements BookRepoCustom {

    private MongoTemplate mongoTemplate;

    BookRepoCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Book> searchBooks(SearchBooksRequest request) {
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
        if (!StringUtils.isEmpty(request.getTitle())) {
            criterias.add(Criteria.where("title").regex(String.format("^%s", request.getTitle()), "i"));
        }
        if (!StringUtils.isEmpty(request.getIsbn13())) {
            criterias.add(Criteria.where("isbn13").is(request.getIsbn13()));
        }
        if (!StringUtils.isEmpty(request.getIsbn10())) {
            criterias.add(Criteria.where("isbn10").is(request.getIsbn10()));
        }
        if (!StringUtils.isEmpty(request.getPublisher())) {
            criterias.add(Criteria.where("publisher").regex(String.format("^%s", request.getPublisher()), "i"));
        }
        if (request.getPublishDateStart() != null) {
            criterias.add(Criteria.where("createdAt").gte(request.getPublishDateStart()));
        }
        if (request.getPublishDateEnd() != null) {
            criterias.add(Criteria.where("createdAt").lt(request.getPublishDateEnd()));
        }
        if (!criterias.isEmpty()) {
            Criteria bookCriteria = new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()]));
            operations.add(match(bookCriteria));
        }

        criterias = new ArrayList<>();
        if (!StringUtils.isEmpty(request.getAuthorId())) {
            criterias.add(Criteria.where("author.id").is(new ObjectId(request.getAuthorId())));
        }
        if (!StringUtils.isEmpty(request.getAuthorFullName())) {
            criterias.add(Criteria.where("author.fullName").regex(String.format("^%s", request.getAuthorFullName()), "i"));
        }
        if (!criterias.isEmpty()) {
            Criteria authorCriteria = new Criteria().andOperator(criterias.toArray(new Criteria[criterias.size()]));
            operations.add(lookup("author", "authorIds", "_id", "author"));
            operations.add(unwind("author", false));
            operations.add(match(authorCriteria));
        }

        operations.add(sort(Sort.Direction.DESC, "createdAt"));

        TypedAggregation<Book> aggregation = newAggregation(Book.class, operations);
        AggregationResults<Book> results = mongoTemplate.aggregate(aggregation, Book.class);
        return results.getMappedResults();
    }


}

