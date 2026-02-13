package com.ongil.backend.domain.order.dto.response;

public record OrderItemDto(
	Long orderItemId,
	Long productId,
	String brandName,
	String productName,
	String imageUrl,
	String selectedSize,
	String selectedColor,
	Integer quantity,
	Integer priceAtOrder
) {}
