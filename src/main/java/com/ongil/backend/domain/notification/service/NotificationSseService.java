package com.ongil.backend.domain.notification.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ongil.backend.domain.notification.dto.response.NotificationResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationSseService {

	// 유저별 SSE 연결 관리
	private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

	private static final Long SSE_TIMEOUT = 30 * 60 * 1000L; // 30분

	// SSE 구독 생성
	public SseEmitter subscribe(Long userId) {
		// 기존 연결이 있으면 제거
		if (emitters.containsKey(userId)) {
			emitters.get(userId).complete();
			emitters.remove(userId);
		}

		SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
		emitters.put(userId, emitter);

		// 연결 종료 시 제거
		emitter.onCompletion(() -> {
			emitters.remove(userId);
			log.info("SSE 연결 종료 - userId: {}", userId);
		});

		emitter.onTimeout(() -> {
			emitters.remove(userId);
			log.info("SSE 타임아웃 - userId: {}", userId);
		});

		emitter.onError(e -> {
			emitters.remove(userId);
			log.error("SSE 에러 - userId: {}", userId, e);
		});

		// 연결 직후 더미 이벤트 전송 (연결 확인용)
		try {
			emitter.send(SseEmitter.event()
				.name("connect")
				.data("SSE 연결 성공"));
		} catch (IOException e) {
			emitters.remove(userId);
			log.error("SSE 초기 이벤트 전송 실패 - userId: {}", userId, e);
		}

		log.info("SSE 연결 생성 - userId: {}", userId);
		return emitter;
	}

	// 알림 전송
	public void sendNotification(Long userId, NotificationResponse notification) {
		SseEmitter emitter = emitters.get(userId);

		if (emitter == null) {
			log.debug("SSE 연결 없음 - userId: {}", userId);
			return;
		}

		try {
			emitter.send(SseEmitter.event()
				.name("price-alert")
				.data(notification));
			log.info("SSE 알림 전송 성공 - userId: {}, notificationId: {}", userId, notification.getNotificationId());
		} catch (IOException e) {
			emitters.remove(userId);
			log.error("SSE 알림 전송 실패 - userId: {}", userId, e);
		}
	}

	// 현재 연결된 사용자 수 조회 (디버깅용)
	public int getConnectedUserCount() {
		return emitters.size();
	}
}
