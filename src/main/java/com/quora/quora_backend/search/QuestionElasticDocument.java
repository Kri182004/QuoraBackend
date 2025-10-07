package com.quora.quora_backend.search;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "questions")
public class QuestionElasticDocument {
    @Id
    private String id;

    @Field(type=FieldType.Text, name="title")
    private String title;

    @Field(type=FieldType.Text, name="questionBody")
    private String questionBody;
    
    @Field(type = FieldType.Text, name = "username")
    private String username;


}
