package com.ongil.backend.domain.order.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.ongil.backend.domain.order.enums.CancelReason;
import com.ongil.backend.domain.order.enums.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 취소 완료 응답")
public record OrderCancelResponse(

	@Schema(description = "주문 ID")
	Long orderId,

	@Schema(description = "주문 번호")
	String orderNumber,

	@Schema(description = "주문 상태")
	OrderStatus orderStatus,

	@Schema(description = "취소 일시")
	LocalDateTime canceledAt,

	@Schema(description = "취소 사유")
	CancelReason cancelReason,

	@Schema(description = "취소 상세 사유")
	String cancelDetail,

	@Schema(description = "주문 상품 목록")
	List<OrderItemDto> orderItems,

	@Schema(description = "환불 정보")
	RefundInfoDto refundInfo,

	@Schema(description = "배송 주소")
	String deliveryAddress,

	@Schema(description = "수령인")
	String recipient,

	@Schema(description = "수령인 연락처")
	String recipientPhone,

	@Schema(description = "배송 메시지")
	String deliveryMessage,

	@Schema(description = "주문 일시")
	LocalDateTime createdAt
) {
}
