package com.edusync.common.constants;

/**
 * Centralized reputation scoring rules.
 * Change weights here to adjust the entire system.
 */
public final class ReputationConstants {

    private ReputationConstants() {}

    public static final int POST_UPVOTE_RECEIVED     =  10;
    public static final int POST_DOWNVOTE_RECEIVED    =  -2;
    public static final int COMMENT_UPVOTE_RECEIVED   =   5;
    public static final int COMMENT_DOWNVOTE_RECEIVED =  -1;
    public static final int BEST_ANSWER_GIVEN         =  15;
}
