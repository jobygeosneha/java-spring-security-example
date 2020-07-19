package io.example.repository;

import io.example.domain.dto.SearchBooksRequest;
import io.example.domain.exception.NotFoundException;
import io.example.domain.model.Book;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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
        Criteria bookCriteria = new Criteria();
        if (!StringUtils.isEmpty(request.getId())) {
            bookCriteria = Criteria.where("id").is(new ObjectId(request.getId()));
        }
        if (!StringUtils.isEmpty(request.getCreatorId())) {
            bookCriteria = bookCriteria.and("creatorId").is(new ObjectId(request.getCreatorId()));
        }
        if (request.getCreatedAtStart() != null) {
            bookCriteria = bookCriteria.and("createdAt").gte(request.getCreatedAtStart());
        }
        if (request.getCreatedAtEnd() != null) {
            bookCriteria = bookCriteria.and("createdAt").lt(request.getCreatedAtEnd());
        }
        if (!StringUtils.isEmpty(request.getTitle())) {
            bookCriteria = bookCriteria.and("title").regex(String.format("^%s", request.getTitle()), "i");
        }
        if (!StringUtils.isEmpty(request.getIsbn13())) {
            bookCriteria = bookCriteria.and("isbn13").is(request.getIsbn13());
        }
        if (!StringUtils.isEmpty(request.getIsbn10())) {
            bookCriteria = bookCriteria.and("isbn10").is(request.getIsbn10());
        }
        if (!StringUtils.isEmpty(request.getPublisher())) {
            bookCriteria = bookCriteria.and("publisher").regex(String.format("^%s", request.getPublisher()), "i");
        }
        if (request.getPublishDateStart() != null) {
            bookCriteria = bookCriteria.and("createdAt").gte(request.getPublishDateStart());
        }
        if (request.getPublishDateEnd() != null) {
            bookCriteria = bookCriteria.and("createdAt").lt(request.getPublishDateEnd());
        }

        Criteria authorCriteria = new Criteria();
        if (!StringUtils.isEmpty(request.getAuthorId())) {
            authorCriteria.and("author.id").is(new ObjectId(request.getAuthorId()));
        }
        if (!StringUtils.isEmpty(request.getAuthorFullName())) {
            authorCriteria.and("author.fullName").regex(String.format("^%s", request.getAuthorFullName()), "i");
        }

        TypedAggregation<Book> aggregation = newAggregation(
                Book.class,
                match(bookCriteria),
                lookup("author", "authorIds", "_id", "author"),
                unwind("author", false),
                match(authorCriteria),
                sort(Sort.Direction.DESC, "createdAt")
        );

        AggregationResults<Book> results = mongoTemplate.aggregate(aggregation, Book.class);
        return results.getMappedResults();
    }


}

