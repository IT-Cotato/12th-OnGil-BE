package com.ongil.backend.domain.order.dto.request;

public record OrderItemRequest(
	Long productId,
	String selectedSize,
	String selectedColor,
	Integer quantity
) {}
