package com.edusync.model.dto.response;

import com.edusync.common.enums.JoinRequestStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class JoinRequestResponse {
    private Long id;
    private Long groupId;
    private String groupName;
    private Long userId;
    private String username;
    private JoinRequestStatus status;
    private LocalDateTime createdAt;
}
