package io.example.domain.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class Author implements Serializable {

    @Id
    private String id;

    @CreatedBy
    private String creatorId;
    @LastModifiedBy
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