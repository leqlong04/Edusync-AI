package com.edusync.controller;

import com.edusync.exception.AppException;
import com.edusync.model.dto.request.CreateGroupRequest;
import com.edusync.model.dto.response.ApiResponse;
import com.edusync.model.dto.response.GroupResponse;
import com.edusync.model.entity.User;
import com.edusync.repository.UserRepository;
import com.edusync.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        GroupResponse response = groupService.createGroup(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/discovery")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getDiscoveryGroups(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success(groupService.getDiscoveryGroups(currentUser.getId())));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getMyGroups(
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        return ResponseEntity.ok(ApiResponse.success(groupService.getUserGroups(currentUser.getId())));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponse<String>> joinGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        String resultMessage = groupService.joinGroup(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(resultMessage));
    }

    @PostMapping("/join-by-code/{joinCode}")
    public ResponseEntity<ApiResponse<String>> joinGroupByCode(
            @PathVariable String joinCode,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        groupService.joinGroupByCode(joinCode, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Joined group successfully using code"));
    }

    @DeleteMapping("/{id}/leave")
    public ResponseEntity<ApiResponse<String>> leaveGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        groupService.leaveGroup(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Left group successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteGroup(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        groupService.deleteGroup(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Group deleted successfully"));
    }

    @PostMapping("/requests/{requestId}/approve")
    public ResponseEntity<ApiResponse<String>> approveJoinRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        groupService.approveJoinRequest(requestId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Join request approved"));
    }

    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<String>> rejectJoinRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = getCurrentUser(userDetails);
        groupService.rejectJoinRequest(requestId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Join request rejected"));
    }

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }
}
