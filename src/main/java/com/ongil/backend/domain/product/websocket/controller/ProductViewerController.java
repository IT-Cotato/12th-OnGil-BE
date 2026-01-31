package com.ongil.backend.domain.product.websocket.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.ongil.backend.domain.product.websocket.service.ProductViewerService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 상품 실시간 조회 인원 WebSocket 컨트롤러
 * <p>
 * 클라이언트의 입장/퇴장 메시지를 처리합니다.
 * <p>
 * [사용 흐름]
 * 1. 클라이언트가 /ws 엔드포인트로 WebSocket 연결
 * 2. /topic/products/{productId}/viewers 구독
 * 3. /app/products/{productId}/enter 로 입장 메시지 전송
 * 4. 서버가 현재 인원 수를 구독자들에게 브로드캐스트
 * 5. 페이지 이탈 시 /app/products/{productId}/leave 로 퇴장 메시지 전송
 */
@Tag(name = "Product WebSocket", description = "상품 실시간 조회 인원 WebSocket API(스웨거 테스트는 불가)")
@Slf4j
@Controller
@RequiredArgsConstructor
public class ProductViewerController {

	private final ProductViewerService productViewerService;

	/**
	 * 상품 상세 페이지 입장 처리
	 * <p>
	 * 클라이언트가 /app/products/{productId}/enter 로 메시지를 보내면 호출됩니다.
	 *
	 * @param productId      상품 ID (URL path에서 추출)
	 * @param headerAccessor WebSocket 세션 정보 접근용
	 */
	@MessageMapping("/products/{productId}/enter")
	public void enterProduct(
		@DestinationVariable Long productId,
		SimpMessageHeaderAccessor headerAccessor
	) {
		String sessionId = headerAccessor.getSessionId();

		// 세션에 현재 보고 있는 상품 ID 저장 (비정상 종료 시 정리용)
		headerAccessor.getSessionAttributes().put("productId", productId);

		productViewerService.addViewer(productId, sessionId);
		log.info("상품 입장: productId={}, sessionId={}", productId, sessionId);
	}

	/**
	 * 상품 상세 페이지 퇴장 처리
	 * <p>
	 * 클라이언트가 /app/products/{productId}/leave 로 메시지를 보내면 호출됩니다.
	 * 정상적인 페이지 이탈 시 호출됩니다.
	 *
	 * @param productId      상품 ID (URL path에서 추출)
	 * @param headerAccessor WebSocket 세션 정보 접근용
	 */
	@MessageMapping("/products/{productId}/leave")
	public void leaveProduct(
		@DestinationVariable Long productId,
		SimpMessageHeaderAccessor headerAccessor
	) {
		String sessionId = headerAccessor.getSessionId();

		// 세션에서 상품 ID 제거
		headerAccessor.getSessionAttributes().remove("productId");

		productViewerService.removeViewer(productId, sessionId);
		log.info("상품 퇴장: productId={}, sessionId={}", productId, sessionId);
	}
}
