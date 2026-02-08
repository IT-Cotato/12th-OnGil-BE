package com.ongil.backend.domain.order.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.ongil.backend.domain.order.enums.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 요약 정보")
public record OrderSummaryDto(
	@Schema(description = "주문 ID")
	Long orderId,

	@Schema(description = "주문 번호")
	String orderNumber,

	@Schema(description = "주문 상태")
	OrderStatus orderStatus,

	@Schema(description = "총 결제 금액")
	Integer totalAmount,

	@Schema(description = "주문 일시")
	LocalDateTime orderDate,

	@Schema(description = "주문 상품 목록")
	List<OrderItemSummaryDto> items
) {
}
