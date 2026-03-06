package com.edusync.repository;

import com.edusync.common.enums.JoinRequestStatus;
import com.edusync.model.entity.GroupJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupJoinRequestRepository extends JpaRepository<GroupJoinRequest, Long> {
    Optional<GroupJoinRequest> findByGroupIdAndUserIdAndStatus(Long groupId, Long userId, JoinRequestStatus status);
    boolean existsByGroupIdAndUserIdAndStatus(Long groupId, Long userId, JoinRequestStatus status);
    List<GroupJoinRequest> findByGroupIdAndStatus(Long groupId, JoinRequestStatus status);
}
