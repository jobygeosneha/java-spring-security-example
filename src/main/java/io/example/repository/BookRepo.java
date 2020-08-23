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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
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

        List<Criteria> criteriaList = new ArrayList<>();
        if (!StringUtils.isEmpty(request.getId())) {
            criteriaList.add(Criteria.where("id").is(new ObjectId(request.getId())));
        }
        if (!StringUtils.isEmpty(request.getCreatorId())) {
            criteriaList.add(Criteria.where("creatorId").is(new ObjectId(request.getCreatorId())));
        }
        if (request.getCreatedAtStart() != null) {
            criteriaList.add(Criteria.where("createdAt").gte(request.getCreatedAtStart()));
        }
        if (request.getCreatedAtEnd() != null) {
            criteriaList.add(Criteria.where("createdAt").lt(request.getCreatedAtEnd()));
        }
        if (!StringUtils.isEmpty(request.getTitle())) {
            criteriaList.add(Criteria.where("title").regex(request.getTitle(), "i"));
        }
        if (!CollectionUtils.isEmpty(request.getGenres())) {
            criteriaList.add(Criteria.where("genres").all(request.getGenres()));
        }
        if (!StringUtils.isEmpty(request.getIsbn13())) {
            criteriaList.add(Criteria.where("isbn13").is(request.getIsbn13()));
        }
        if (!StringUtils.isEmpty(request.getIsbn10())) {
            criteriaList.add(Criteria.where("isbn10").is(request.getIsbn10()));
        }
        if (!StringUtils.isEmpty(request.getPublisher())) {
            criteriaList.add(Criteria.where("publisher").regex(request.getPublisher(), "i"));
        }
        if (request.getPublishDateStart() != null) {
            criteriaList.add(Criteria.where("publishDate").gte(request.getPublishDateStart()));
        }
        if (request.getPublishDateEnd() != null) {
            criteriaList.add(Criteria.where("publishDate").lt(request.getPublishDateEnd()));
        }
        if (!criteriaList.isEmpty()) {
            Criteria bookCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
            operations.add(match(bookCriteria));
        }

        criteriaList = new ArrayList<>();
        if (!StringUtils.isEmpty(request.getAuthorId())) {
            criteriaList.add(Criteria.where("author._id").is(new ObjectId(request.getAuthorId())));
        }
        if (!StringUtils.isEmpty(request.getAuthorFullName())) {
            criteriaList.add(Criteria.where("author.fullName").regex(request.getAuthorFullName(), "i"));
        }
        if (!criteriaList.isEmpty()) {
            Criteria authorCriteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
            operations.add(lookup("authors", "authorIds", "_id", "author"));
            operations.add(unwind("author", false));
            operations.add(match(authorCriteria));
        }

        operations.add(sort(Sort.Direction.DESC, "createdAt"));
        operations.add(skip((request.getPage() - 1) * request.getLimit()));
        operations.add(limit(request.getLimit()));

        TypedAggregation<Book> aggregation = newAggregation(Book.class, operations);
        System.out.println(aggregation.toString());
        AggregationResults<Book> results = mongoTemplate.aggregate(aggregation, Book.class);
        return results.getMappedResults();
    }


}

