package com.edusync.controller;

import com.edusync.model.dto.response.ApiResponse;
import com.edusync.model.dto.response.NotificationResponse;
import com.edusync.model.entity.User;
import com.edusync.security.CurrentUser;
import com.edusync.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(@CurrentUser User currentUser) {
        List<NotificationResponse> responses = notificationService.getUserNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(@CurrentUser User currentUser) {
        long count = notificationService.getUnreadCount(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("count", count)));
    }

    @PatchMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@CurrentUser User currentUser) {
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
