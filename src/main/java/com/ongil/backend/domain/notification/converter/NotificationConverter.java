package com.ongil.backend.domain.notification.converter;

import com.ongil.backend.domain.notification.dto.response.NotificationResponse;
import com.ongil.backend.domain.notification.entity.Notification;

public class NotificationConverter {

	public static NotificationResponse toResponse(Notification notification) {
		return NotificationResponse.builder()
			.notificationId(notification.getId())
			.message(notification.getMessage())
			.targetUrl(notification.getTargetUrl())
			.read(notification.getIsRead())
			.notifiedAt(notification.getNotifiedAt())
			.build();
	}
}