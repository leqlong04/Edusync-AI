package com.edusync.model.dto.response;

import com.edusync.common.enums.GroupVisibility;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class GroupResponse {
    private Long id;
    private String name;
    private String description;
    private String ownerName;
    private GroupVisibility visibility;
    private String joinCode;
    private LocalDateTime createdAt;
}
