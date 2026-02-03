package com.ongil.backend.domain.notification.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ongil.backend.domain.notification.dto.response.NotificationResponse;
import com.ongil.backend.domain.notification.service.NotificationService;
import com.ongil.backend.domain.notification.service.NotificationSseService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Notification", description = "알림 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;
	private final NotificationSseService notificationSseService;

	@Operation(summary = "SSE 연결", description = "실시간 알림을 받기 위한 SSE 연결. 연결 유지 필요.")
	@GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribe(@AuthenticationPrincipal Long userId) {
		return notificationSseService.subscribe(userId);
	}

	@Operation(summary = "알림 목록 조회", description = "읽지 않은 알림 목록을 조회합니다.")
	@GetMapping
	public DataResponse<List<NotificationResponse>> getNotifications(
		@AuthenticationPrincipal Long userId) {

		List<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId);
		return DataResponse.from(notifications);
	}

	@Operation(summary = "알림 개수 조회", description = "읽지 않은 알림 개수를 조회합니다. (뱃지 표시용)")
	@GetMapping("/count")
	public DataResponse<Map<String, Long>> getUnreadCount(
		@AuthenticationPrincipal Long userId) {

		long count = notificationService.getUnreadCount(userId);
		return DataResponse.from(Map.of("count", count));
	}

	@Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
	@PatchMapping("/{notificationId}/read")
	public DataResponse<Void> markAsRead(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long notificationId) {

		notificationService.markAsRead(userId, notificationId);
		return DataResponse.ok();
	}

	@Operation(summary = "모든 알림 읽음 처리", description = "모든 알림을 읽음 처리합니다.")
	@PatchMapping("/read-all")
	public DataResponse<Void> markAllAsRead(
		@AuthenticationPrincipal Long userId) {

		notificationService.markAllAsRead(userId);
		return DataResponse.ok();
	}
}
