package com.edusync.repository;

import com.edusync.model.entity.ReputationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReputationLogRepository extends JpaRepository<ReputationLog, Long> {
    List<ReputationLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}
