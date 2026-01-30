package com.ongil.backend.domain.order.dto.request;

import java.util.List;

public record OrderCreateRequest(
	List<OrderItemRequest> items,
	Integer usedPoints,
	String recipient,
	String recipientPhone,
	String deliveryAddress,
	String detailAddress,
	String postalCode,
	String deliveryMessage
) {}