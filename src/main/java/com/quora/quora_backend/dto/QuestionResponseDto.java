// In QuestionResponseDto.java

package com.quora.quora_backend.dto;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDto {

    private String id;
    private String title;
    private String questionBody;
    private String userId;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}