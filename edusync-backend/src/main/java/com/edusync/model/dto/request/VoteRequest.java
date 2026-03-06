package com.edusync.model.dto.request;

import com.edusync.common.enums.VoteType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteRequest {
    @NotNull(message = "Vote type is required")
    private VoteType type;
}
