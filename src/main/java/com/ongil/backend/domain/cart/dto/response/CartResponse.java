package com.ongil.backend.domain.cart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "장바구니 응답")
public class CartResponse {

	@Schema(description = "장바구니 ID")
	private Long cartId;

	@Schema(description = "상품 ID")
	private Long productId;

	@Schema(description = "상품명")
	private String productName;

	@Schema(description = "브랜드명")
	private String brandName;

	@Schema(description = "대표 이미지 URL")
	private String thumbnailImageUrl;

	// 선택한 옵션
	@Schema(description = "선택한 사이즈")
	private String selectedSize;

	@Schema(description = "선택한 색상")
	private String selectedColor;

	@Schema(description = "수량")
	private Integer quantity;

	// 가격 정보
	@Schema(description = "개당 가격 (할인 적용)")
	private Integer price;

	@Schema(description = "총 가격 (수량 * 개당 가격)")
	private Integer totalPrice;
}