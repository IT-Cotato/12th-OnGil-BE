package com.ongil.backend.domain.notification.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	// 해당 사용자의 읽지 않은 알림 전체 조회 (알림 탭용)
	List<Notification> findByUserIdAndIsReadFalseOrderByNotifiedAtDesc(Long userId);

	// 해당 사용자의 읽지 않은 알림 개수 조회 (알림 아이콘 뱅지용)
	long countByUserIdAndIsReadFalse(Long userId);
}