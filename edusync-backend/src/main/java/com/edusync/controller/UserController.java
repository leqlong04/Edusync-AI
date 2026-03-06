package com.edusync.controller;

import com.edusync.model.dto.response.ApiResponse;
import com.edusync.model.dto.response.UserProfileResponse;
import com.edusync.model.entity.User;
import com.edusync.security.CurrentUser;
import com.edusync.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final com.edusync.service.GroupService groupService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(@CurrentUser User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserProfile(currentUser)));
    }

    @GetMapping("/me/groups")
    public ResponseEntity<ApiResponse<java.util.List<com.edusync.model.dto.response.GroupResponse>>> getMyGroups(@CurrentUser User currentUser) {
        return ResponseEntity.ok(ApiResponse.success(groupService.getUserGroups(currentUser.getId())));
    }
}
