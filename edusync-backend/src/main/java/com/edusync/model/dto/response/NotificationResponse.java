package com.edusync.model.dto.response;

import com.edusync.common.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String senderUsername;
    private NotificationType type;
    private String targetUrl;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
