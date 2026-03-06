package com.edusync.controller;

import com.edusync.model.dto.request.CreateCommentRequest;
import com.edusync.model.dto.response.ApiResponse;
import com.edusync.model.dto.response.CommentResponse;
import com.edusync.model.entity.User;
import com.edusync.security.CurrentUser;
import com.edusync.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request,
            @CurrentUser User currentUser) {
        CommentResponse response = commentService.createComment(postId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable Long postId) {
        List<CommentResponse> responses = commentService.getPostComments(postId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{commentId}/best-answer")
    public ResponseEntity<ApiResponse<Void>> markBestAnswer(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @CurrentUser User currentUser) {
        commentService.markBestAnswer(commentId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @CurrentUser User currentUser) {
        commentService.deleteComment(commentId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
