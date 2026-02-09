package com.ongil.backend.domain.order.dto.response;

import java.time.LocalDateTime;

import com.ongil.backend.domain.order.enums.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 취소 응답")
public record OrderCancelResponse(
	@Schema(description = "주문 ID")
	Long orderId,

	@Schema(description = "주문 번호")
	String orderNumber,

	@Schema(description = "주문 상태")
	OrderStatus orderStatus,

	@Schema(description = "취소 일시")
	LocalDateTime canceledAt,

	@Schema(description = "환불 예정 금액")
	Integer refundAmount
) {}
