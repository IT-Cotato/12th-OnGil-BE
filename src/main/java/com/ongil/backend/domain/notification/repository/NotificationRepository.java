package com.ongil.backend.domain.notification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ongil.backend.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	// 해당 사용자의 읽지 않은 알림 전체 조회 (알림 탭용)
	List<Notification> findByUserIdAndIsReadFalseOrderByNotifiedAtDesc(Long userId);

	// 해당 사용자의 읽지 않은 알림 개수 조회 (알림 아이콘 뱅지용)
	long countByUserIdAndIsReadFalse(Long userId);

	// 해당 사용자의 읽지 않은 알림 전체 읽음 처리 (벌크 UPDATE)
	@Modifying
	@Query("UPDATE Notification n SET n.isRead = true, n.readAt = :now WHERE n.user.id = :userId AND n.isRead = false")
	void markAllAsRead(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}