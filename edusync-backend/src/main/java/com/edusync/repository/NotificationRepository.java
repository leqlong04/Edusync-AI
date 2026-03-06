package com.edusync.repository;

import com.edusync.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n JOIN FETCH n.sender WHERE n.receiver.id = :userId ORDER BY n.createdAt DESC")
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    long countByReceiverIdAndIsReadFalse(Long receiverId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.receiver.id = :userId AND n.isRead = false")
    void markAllAsRead(@Param("userId") Long userId);
}
