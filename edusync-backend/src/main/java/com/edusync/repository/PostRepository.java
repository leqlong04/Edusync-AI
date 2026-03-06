package com.edusync.repository;

import com.edusync.model.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p JOIN FETCH p.user u WHERE p.group.id = :groupId ORDER BY p.createdAt DESC")
    List<Post> findByGroupIdWithUserOrderByCreatedAtDesc(@Param("groupId") Long groupId);
}
