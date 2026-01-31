package com.ongil.backend.global.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket + STOMP 설정
 * <p>
 * STOMP(Simple Text Oriented Messaging Protocol)를 사용하여
 * 메시지 기반의 양방향 통신을 구현합니다.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	/**
	 * STOMP 엔드포인트 등록
	 * 클라이언트는 이 엔드포인트로 WebSocket 연결을 수립합니다.
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws") // WebSocket 엔드포인트 URL(연결 url)
			.setAllowedOriginPatterns("*")
			.withSockJS();
	}

	/**
	 * 메시지 브로커 설정
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 서버 -> 클라이언트, 구독용
		registry.enableSimpleBroker("/topic");
		// 클라이언트 -> 서버
		registry.setApplicationDestinationPrefixes("/app");
	}
}
