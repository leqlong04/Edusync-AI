package com.edusync.model.dto.response;

import com.edusync.common.enums.ContentType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PostResponse {
    private Long id;
    private Long groupId;
    private Long userId;
    private String username;
    private String title;
    private String description;
    private ContentType contentType;
    private LocalDateTime createdAt;
    
    // Snippet details
    private String codeContent;
    private String language;
    private String explanationAi;
    
    // Flashcard details
    private String frontSide;
    private String backSide;
}
