package com.ongil.backend.domain.order.dto.response;

import java.time.LocalDateTime;

public record OrderCancelResponse(
	Long orderId,
	String orderNumber,
	String orderStatus,
	Integer refundedPoints,
	LocalDateTime canceledAt
) {}
