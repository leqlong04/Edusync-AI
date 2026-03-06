package com.edusync.controller;

import com.edusync.model.dto.request.CreatePostRequest;
import com.edusync.model.dto.response.ApiResponse;
import com.edusync.model.dto.response.PostResponse;
import com.edusync.model.entity.User;
import com.edusync.security.CurrentUser;
import com.edusync.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/groups/{groupId}/posts")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @PathVariable Long groupId,
            @Valid @RequestBody CreatePostRequest request,
            @CurrentUser User currentUser) {
        PostResponse response = postService.createPost(groupId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/groups/{groupId}/posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getGroupPosts(
            @PathVariable Long groupId,
            @CurrentUser User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(postService.getGroupPosts(groupId, currentUser)));
    }

    @GetMapping({"/groups/{groupId}/posts/{postId}", "/posts/{postId}"})
    public ResponseEntity<ApiResponse<PostResponse>> getPostDetail(
            @PathVariable(required = false) Long groupId,
            @PathVariable Long postId,
            @CurrentUser User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(postService.getPostDetail(groupId != null ? groupId : 0L, postId, currentUser)));
    }

    @PutMapping({"/groups/{groupId}/posts/{postId}", "/posts/{postId}"})
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable(required = false) Long groupId,
            @PathVariable Long postId,
            @Valid @RequestBody CreatePostRequest request,
            @CurrentUser User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(postService.updatePost(groupId != null ? groupId : 0L, postId, request, currentUser)));
    }

    @DeleteMapping({"/groups/{groupId}/posts/{postId}", "/posts/{postId}"})
    public ResponseEntity<ApiResponse<String>> deletePost(
            @PathVariable(required = false) Long groupId,
            @PathVariable Long postId,
            @CurrentUser User currentUser) {
        postService.deletePost(groupId != null ? groupId : 0L, postId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Post deleted successfully"));
    }
}
