package com.edusync.model.dto.response;

import com.edusync.common.enums.VoteType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VoteResponse {
    private long upVotes;
    private long downVotes;
    private int score;
    private VoteType userVote; // UP, DOWN, or null if user hasn't voted
}
