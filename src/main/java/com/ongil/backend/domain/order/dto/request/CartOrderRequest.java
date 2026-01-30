package com.ongil.backend.domain.order.dto.request;

import java.util.List;

public record CartOrderRequest(
	List<Long> cartItemIds,
	Integer usedPoints,
	String recipient,
	String recipientPhone,
	String deliveryAddress,
	String detailAddress,
	String postalCode,
	String deliveryMessage
) {}
