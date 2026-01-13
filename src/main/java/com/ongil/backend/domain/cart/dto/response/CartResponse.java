package com.ongil.backend.domain.cart.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CartResponse {

	private Long cartId;
	private Long productId;
	private String productName;
	private String brandName;
	private String thumbnailImageUrl;

	// 선택한 옵션
	private String selectedSize;
	private String selectedColor;
	private Integer quantity;

	// 가격 정보
	private Integer price;          // 개당 가격
	private Integer totalPrice;     // 수량 * 가격
}
