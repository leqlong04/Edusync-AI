package com.edusync.service;

import com.edusync.model.entity.ReputationLog;
import com.edusync.model.entity.User;
import com.edusync.repository.ReputationLogRepository;
import com.edusync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Single Responsibility: handles only reputation score updates + audit logging.
 * Called by VoteService, CommentService, etc.
 */
@Service
@RequiredArgsConstructor
public class ReputationService {

    private final UserRepository userRepository;
    private final ReputationLogRepository reputationLogRepository;

    @Transactional
    public void addPoints(User targetUser, int points, String actionType) {
        int current = targetUser.getReputationScore() != null ? targetUser.getReputationScore() : 0;
        targetUser.setReputationScore(current + points);
        userRepository.save(targetUser);

        ReputationLog log = ReputationLog.builder()
                .user(targetUser)
                .actionType(actionType)
                .points(points)
                .build();
        reputationLogRepository.save(log);
    }
}
