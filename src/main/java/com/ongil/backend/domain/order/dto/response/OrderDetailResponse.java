package com.ongil.backend.domain.order.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.ongil.backend.domain.order.enums.OrderStatus;

public record OrderDetailResponse(
	Long id,
	String orderNumber,
	List<OrderItemDto> orderItems,
	Integer totalAmount,
	OrderStatus orderStatus,

	// 배송 정보
	String recipient,
	String recipientPhone,
	String deliveryAddress,
	String detailAddress,
	String postalCode,
	String deliveryMessage,

	// 주문 상태 변경 시간
	LocalDateTime createdAt,
	LocalDateTime shippingStartedAt,
	LocalDateTime deliveredAt,
	LocalDateTime confirmedAt,
	LocalDateTime canceledAt
) {}