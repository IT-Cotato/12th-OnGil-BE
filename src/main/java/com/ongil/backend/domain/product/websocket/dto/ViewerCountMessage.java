package com.ongil.backend.domain.product.websocket.dto;

/**
 * 실시간 조회 인원 수 메시지 DTO
 *
 * 클라이언트에게 브로드캐스트되는 메시지 형식입니다.
 */
public record ViewerCountMessage(
	Long productId,
	Long viewerCount
) {
	public static ViewerCountMessage of(Long productId, Long viewerCount) {
		return new ViewerCountMessage(productId, viewerCount);
	}
}
