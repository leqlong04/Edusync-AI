package com.edusync.repository;

import com.edusync.model.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMember.GroupMemberId> {

    @Query("""
        SELECT gm FROM GroupMember gm
        JOIN FETCH gm.group g
        JOIN FETCH g.owner
        WHERE gm.user.id = :userId
    """)
    List<GroupMember> findGroupsByUserIdWithDetails(@Param("userId") Long userId);

    List<GroupMember> findByUserId(Long userId);
    List<GroupMember> findByGroupId(Long groupId);
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
}
