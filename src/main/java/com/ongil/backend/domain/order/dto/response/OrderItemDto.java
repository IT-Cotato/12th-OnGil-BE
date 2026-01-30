package com.ongil.backend.domain.order.dto.response;

public record OrderItemDto(
	String brandName,
	String productName,
	String selectedSize,
	String selectedColor,
	Integer quantity,
	Integer priceAtOrder
) {}
