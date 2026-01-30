package com.ongil.backend.domain.order.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
	Long id,
	String orderNumber,
	List<OrderItemDto> orderItems,
	Integer totalAmount,

	// 배송 정보
	String recipient,
	String recipientPhone,
	String deliveryAddress,
	String deliveryMessage,

	LocalDateTime createdAt
) {}