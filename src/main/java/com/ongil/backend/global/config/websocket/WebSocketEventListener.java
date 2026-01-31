package com.ongil.backend.global.config.websocket;

import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.ongil.backend.domain.product.websocket.service.ProductViewerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket 세션 이벤트 리스너
 * <p>
 * 클라이언트의 연결/해제 이벤트를 감지하여 처리합니다.
 * 특히 비정상 종료(브라우저 강제 종료 등) 시에도 세션 정리가 가능합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

	private final ProductViewerService productViewerService;

	// WebSocket 연결 시 호출
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = headerAccessor.getSessionId();
		log.debug("WebSocket 연결: sessionId={}", sessionId);
	}

	/**
	 * WebSocket 연결 해제 시 호출
	 * 정상 or 비정상(브라우저 종료, 네트워크 끊김 등) 모든 종료 상황에서 호출됩니다.
	 */
	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		String sessionId = headerAccessor.getSessionId();

		// 세션에 저장된 productId가 있으면 조회 인원에서 제거
		Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
		if (sessionAttributes == null) {
			log.debug("Session attributes is null. sessionId={}", sessionId);
			return;
		}

		Long productId = (Long)sessionAttributes.get("productId");
		if (productId != null) {
			productViewerService.removeViewer(productId, sessionId);
			log.debug("비정상 종료로 인한 viewer 제거: productId={}, sessionId={}", productId, sessionId);
		}
	}
}
