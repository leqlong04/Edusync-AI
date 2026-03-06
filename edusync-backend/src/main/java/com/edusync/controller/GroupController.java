package com.edusync.controller;

import com.edusync.model.dto.request.CreateGroupRequest;
import com.edusync.model.dto.response.ApiResponse;
import com.edusync.model.dto.response.GroupResponse;
import com.edusync.model.dto.response.JoinRequestResponse;
import com.edusync.model.entity.User;
import com.edusync.security.CurrentUser;
import com.edusync.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(
            @Valid @RequestBody CreateGroupRequest request,
            @CurrentUser User currentUser) {
        GroupResponse response = groupService.createGroup(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/discovery")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getDiscoveryGroups(
            @CurrentUser User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(groupService.getDiscoveryGroups(currentUser.getId())));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getMyGroups(
            @CurrentUser User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(groupService.getUserGroups(currentUser.getId())));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<ApiResponse<String>> joinGroup(
            @PathVariable Long id,
            @CurrentUser User currentUser) {
        String resultMessage = groupService.joinGroup(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success(resultMessage));
    }

    @PostMapping("/join-by-code")
    public ResponseEntity<ApiResponse<String>> joinGroupByCode(
            @RequestParam String code,
            @CurrentUser User currentUser) {
        groupService.joinGroupByCode(code, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Joined group successfully using code"));
    }

    @DeleteMapping("/{id}/members/me")
    public ResponseEntity<ApiResponse<String>> leaveGroup(
            @PathVariable Long id,
            @CurrentUser User currentUser) {
        groupService.leaveGroup(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Left group successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteGroup(
            @PathVariable Long id,
            @CurrentUser User currentUser) {
        groupService.deleteGroup(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Group deleted successfully"));
    }

    @GetMapping("/{groupId}/join-requests")
    public ResponseEntity<ApiResponse<List<JoinRequestResponse>>> getJoinRequests(
            @PathVariable Long groupId,
            @CurrentUser User currentUser) {
        List<JoinRequestResponse> requests = groupService.getJoinRequests(groupId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    @PostMapping("/{groupId}/join-requests/{requestId}/approve")
    public ResponseEntity<ApiResponse<String>> approveJoinRequest(
            @PathVariable Long groupId,
            @PathVariable Long requestId,
            @CurrentUser User currentUser) {
        groupService.approveJoinRequest(groupId, requestId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Join request approved"));
    }

    @PostMapping("/{groupId}/join-requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<String>> rejectJoinRequest(
            @PathVariable Long groupId,
            @PathVariable Long requestId,
            @CurrentUser User currentUser) {
        groupService.rejectJoinRequest(groupId, requestId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Join request rejected"));
    }
}
