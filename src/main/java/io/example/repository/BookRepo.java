package io.example.repository;

import io.example.domain.model.Book;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepo extends MongoRepository<Book, ObjectId> {

    @Aggregation("{ $lookup: { from: 'author', localField: 'authorIds', foreignField: '_id', as: 'authors' } }")
    List<Book> findBooksWithAuthors(Pageable pageable);

}
