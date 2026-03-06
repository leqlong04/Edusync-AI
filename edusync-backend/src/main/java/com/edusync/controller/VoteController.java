package com.edusync.controller;

import com.edusync.common.enums.VoteType;
import com.edusync.model.dto.response.ApiResponse;
import com.edusync.model.dto.response.VoteResponse;
import com.edusync.model.entity.User;
import com.edusync.security.CurrentUser;
import com.edusync.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<ApiResponse<VoteResponse>> toggleVote(
            @PathVariable Long postId,
            @jakarta.validation.Valid @RequestBody com.edusync.model.dto.request.VoteRequest request,
            @CurrentUser User currentUser) {
        VoteResponse response = voteService.toggleVote(postId, request.getType(), currentUser);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<VoteResponse>> getVoteStats(
            @PathVariable Long postId,
            @CurrentUser User currentUser) {
        VoteResponse response = voteService.getPostVoteStats(postId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
