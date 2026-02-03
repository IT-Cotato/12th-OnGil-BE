package com.ongil.backend.domain.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.notification.converter.NotificationConverter;
import com.ongil.backend.domain.notification.dto.response.NotificationResponse;
import com.ongil.backend.domain.notification.entity.Notification;
import com.ongil.backend.domain.notification.repository.NotificationRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;

	// 읽지 않은 알림 목록 조회
	@Transactional(readOnly = true)
	public List<NotificationResponse> getUnreadNotifications(Long userId) {
		List<Notification> notifications = notificationRepository
			.findByUserIdAndIsReadFalseOrderByNotifiedAtDesc(userId);

		return notifications.stream()
			.map(NotificationConverter::toResponse)
			.toList();
	}

	// 읽지 않은 알림 개수 조회
	@Transactional(readOnly = true)
	public long getUnreadCount(Long userId) {
		return notificationRepository.countByUserIdAndIsReadFalse(userId);
	}

	// 개별 알림 읽음 처리
	@Transactional
	public void markAsRead(Long userId, Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOTIFICATION_NOT_FOUND));

		// 본인의 알림인지 확인
		if (!notification.getUser().getId().equals(userId)) {
			throw new EntityNotFoundException(ErrorCode.NOTIFICATION_NOT_FOUND);
		}

		notification.markAsRead();
	}

	// 모든 알림 읽음 처리
	@Transactional
	public void markAllAsRead(Long userId) {
		List<Notification> notifications = notificationRepository
			.findByUserIdAndIsReadFalseOrderByNotifiedAtDesc(userId);

		notifications.forEach(Notification::markAsRead);
	}
}
