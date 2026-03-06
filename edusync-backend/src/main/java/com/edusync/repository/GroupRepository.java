package com.edusync.repository;

import com.edusync.model.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("SELECT g FROM Group g WHERE g.visibility != 'SECRET' AND g.id NOT IN (SELECT gm.group.id FROM GroupMember gm WHERE gm.user.id = :userId)")
    List<Group> findGroupsUserNotJoined(@Param("userId") Long userId);

    Optional<Group> findByJoinCode(String joinCode);
}
