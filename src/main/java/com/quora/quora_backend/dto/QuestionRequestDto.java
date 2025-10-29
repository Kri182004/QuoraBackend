

package com.quora.quora_backend.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequestDto {
    private List<String> topicNames;
    
    @NotBlank(message = "Title is required")
    @Size(min = 10, max = 100, message = "Title must be between 10 and 100 characters")
    private String title;
    
    @NotBlank(message = "Question body is required")
    private String questionBody;
    //hamne yaha par userId  aur username nahi rakha,
    // kyunki user authentication ke time par hi mil jayega
    //aur hum question ko usi user ke sath link kar denge

}