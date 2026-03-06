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

    @GetMapping("/groups/{groupId}/posts/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPostDetail(
            @PathVariable Long groupId,
            @PathVariable Long id,
            @CurrentUser User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(postService.getPostDetail(groupId, id, currentUser)));
    }

    @DeleteMapping("/groups/{groupId}/posts/{id}")
    public ResponseEntity<ApiResponse<String>> deletePost(
            @PathVariable Long groupId,
            @PathVariable Long id,
            @CurrentUser User currentUser) {
        postService.deletePost(groupId, id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Post deleted successfully"));
    }
}
