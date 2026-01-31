package com.ongil.backend.domain.product.websocket.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.ongil.backend.domain.product.websocket.dto.ViewerCountMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 상품 실시간 조회 인원 관리 서비스
 * <p>
 * Redis Set 자료구조를 사용하여 각 상품별 조회 중인 세션 ID를 관리합니다.
 * - Set을 사용하면 중복 방지 및 O(1) 시간에 추가/삭제가 가능합니다.
 * - TTL을 설정하여 비정상 종료 시에도 일정 시간 후 자동 정리됩니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductViewerService {

	private final StringRedisTemplate redisTemplate;
	private final SimpMessagingTemplate messagingTemplate;

	private static final String VIEWER_KEY_PREFIX = "product:viewers:";
	private static final long VIEWER_TTL_MINUTES = 30;  // 비정상 종료 대비 TTL

	// 상품 조회 입장 처리
	public void addViewer(Long productId, String sessionId) {
		String key = VIEWER_KEY_PREFIX + productId;

		// Redis Set에 세션 ID 추가
		redisTemplate.opsForSet().add(key, sessionId);

		// TTL 갱신 (30분 후 자동 삭제)
		redisTemplate.expire(key, VIEWER_TTL_MINUTES, TimeUnit.MINUTES);

		log.debug("Viewer 추가: productId={}, sessionId={}", productId, sessionId);

		// 변경된 인원 수 브로드캐스트
		broadcastViewerCount(productId);
	}

	// 상품 조회 퇴장 처리
	public void removeViewer(Long productId, String sessionId) {
		String key = VIEWER_KEY_PREFIX + productId;

		// Redis Set에서 세션 ID 제거
		redisTemplate.opsForSet().remove(key, sessionId);

		log.debug("Viewer 제거: productId={}, sessionId={}", productId, sessionId);

		// 변경된 인원 수 브로드캐스트
		broadcastViewerCount(productId);
	}

	// 특정 상품의 현재 조회 인원 수 조회
	public Long getViewerCount(Long productId) {
		String key = VIEWER_KEY_PREFIX + productId;
		Long count = redisTemplate.opsForSet().size(key);
		return count != null ? count : 0L;
	}

	// 변경된 조회 인원 수를 구독자들에게 브로드캐스트
	private void broadcastViewerCount(Long productId) {
		Long viewerCount = getViewerCount(productId);
		ViewerCountMessage message = ViewerCountMessage.of(productId, viewerCount);

		// /topic/products/{productId}/viewers 를 구독 중인 클라이언트에게 전송
		String destination = "/topic/products/" + productId + "/viewers";
		messagingTemplate.convertAndSend(destination, message);

		log.debug("Viewer count 브로드캐스트: productId={}, count={}", productId, viewerCount);
	}
}
