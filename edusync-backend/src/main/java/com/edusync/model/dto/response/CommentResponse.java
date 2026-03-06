package com.edusync.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommentResponse {
    private Long id;
    private Long postId;
    private Long userId;
    private String username;
    private String content;
    private Boolean isBestAnswer;
    private Long parentId;
    private LocalDateTime createdAt;
    private List<CommentResponse> replies;
}
