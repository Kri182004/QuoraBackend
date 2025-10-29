package com.quora.quora_backend.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "questions") // For Elasticsearch
public class Question {
  @DBRef //
    private List<Topic> topics = new ArrayList<>();

  @Id
  private String id;

  private String userId;
  private String username;

  @NotBlank(message = "Title is required")
  @Size(
    min = 10,
    max = 100,
    message = "Title must be between 10 and 100 characters"
  )
  private String title;

  @NotBlank(message = "Question body is required")
  @Size(
    min = 10,
    max = 1000,
    message = "Question body must be between 10 and 1000 characters"
  )
  private String questionBody;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  private LocalDateTime updatedAt;

  private List<Answer> answers = new ArrayList<>();
private Integer voteCount = 0;//added to keep track of the net votes (upvotes - downvotes) for the question
}