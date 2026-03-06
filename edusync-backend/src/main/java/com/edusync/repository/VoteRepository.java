package com.edusync.repository;

import com.edusync.model.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, String targetType);

    long countByTargetIdAndTargetTypeAndVoteType(Long targetId, String targetType,
                                                  com.edusync.common.enums.VoteType voteType);

    void deleteByUserIdAndTargetIdAndTargetType(Long userId, Long targetId, String targetType);
}
