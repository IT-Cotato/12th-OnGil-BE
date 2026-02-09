package com.ongil.backend.domain.order.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "환불 정보 조회 응답")
public record CancelRefundInfoResponse(

	@Schema(description = "주문 상품 목록")
	List<OrderItemDto> orderItems,

	@Schema(description = "환불 정보")
	RefundInfoDto refundInfo
) {
}
