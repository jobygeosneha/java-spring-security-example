package io.example.domain.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class Author implements Serializable {

    @Id
    private String id;

    @DBRef
    private String creatorId;
    @DBRef
    private String modifierId;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    private String fullName;
    private String about;
    private String nationality;
    private Set<String> genres;

    private Set<String> bookIds;

}