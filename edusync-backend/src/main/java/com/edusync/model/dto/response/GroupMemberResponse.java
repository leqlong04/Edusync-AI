package com.edusync.model.dto.response;

import com.edusync.common.enums.GroupRole;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class GroupMemberResponse {
    private Long userId;
    private String username;
    private GroupRole role;
    private LocalDateTime joinedAt;
}
