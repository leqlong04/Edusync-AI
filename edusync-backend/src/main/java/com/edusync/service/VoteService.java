package com.edusync.service;

import com.edusync.common.constants.ReputationConstants;
import com.edusync.common.enums.VoteType;
import com.edusync.exception.AppException;
import com.edusync.model.dto.response.VoteResponse;
import com.edusync.model.entity.Post;
import com.edusync.model.entity.User;
import com.edusync.model.entity.Vote;
import com.edusync.repository.PostRepository;
import com.edusync.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final ReputationService reputationService;

    /**
     * Toggle vote logic (3 states):
     * 1. No existing vote → create new vote
     * 2. Same type exists → remove vote (toggle off)
     * 3. Different type exists → switch vote type
     */
    @Transactional
    public VoteResponse toggleVote(Long postId, VoteType voteType, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException("Post not found", HttpStatus.NOT_FOUND));

        if (post.getUser().getId().equals(currentUser.getId())) {
            throw new AppException("You cannot vote on your own post", HttpStatus.BAD_REQUEST);
        }

        User author = post.getUser();
        Optional<Vote> existingOpt = voteRepository.findByUserIdAndTargetIdAndTargetType(
                currentUser.getId(), postId, "POST");

        if (existingOpt.isPresent()) {
            Vote existing = existingOpt.get();
            if (existing.getVoteType() == voteType) {
                // Toggle OFF: same button clicked → remove vote & reverse reputation
                int reversePoints = (voteType == VoteType.UP)
                        ? -ReputationConstants.POST_UPVOTE_RECEIVED
                        : -ReputationConstants.POST_DOWNVOTE_RECEIVED;
                reputationService.addPoints(author, reversePoints, "VOTE_REMOVED");
                voteRepository.delete(existing);
            } else {
                // Switch: UP→DOWN or DOWN→UP
                int removeOld = (existing.getVoteType() == VoteType.UP)
                        ? -ReputationConstants.POST_UPVOTE_RECEIVED
                        : -ReputationConstants.POST_DOWNVOTE_RECEIVED;
                int addNew = (voteType == VoteType.UP)
                        ? ReputationConstants.POST_UPVOTE_RECEIVED
                        : ReputationConstants.POST_DOWNVOTE_RECEIVED;

                existing.setVoteType(voteType);
                voteRepository.save(existing);
                reputationService.addPoints(author, removeOld + addNew, "VOTE_CHANGED");
            }
        } else {
            // New vote
            Vote newVote = Vote.builder()
                    .user(currentUser)
                    .targetId(postId)
                    .targetType("POST")
                    .voteType(voteType)
                    .build();
            voteRepository.save(newVote);

            int points = (voteType == VoteType.UP)
                    ? ReputationConstants.POST_UPVOTE_RECEIVED
                    : ReputationConstants.POST_DOWNVOTE_RECEIVED;
            reputationService.addPoints(author, points, "UPVOTE_RECEIVED");
        }

        return buildVoteResponse(postId, currentUser.getId());
    }

    @Transactional(readOnly = true)
    public VoteResponse getPostVoteStats(Long postId, Long currentUserId) {
        return buildVoteResponse(postId, currentUserId);
    }

    private VoteResponse buildVoteResponse(Long postId, Long currentUserId) {
        long up = voteRepository.countByTargetIdAndTargetTypeAndVoteType(postId, "POST", VoteType.UP);
        long down = voteRepository.countByTargetIdAndTargetTypeAndVoteType(postId, "POST", VoteType.DOWN);

        VoteType userVote = voteRepository.findByUserIdAndTargetIdAndTargetType(currentUserId, postId, "POST")
                .map(Vote::getVoteType)
                .orElse(null);

        return VoteResponse.builder()
                .upVotes(up)
                .downVotes(down)
                .score((int) (up - down))
                .userVote(userVote)
                .build();
    }
}
