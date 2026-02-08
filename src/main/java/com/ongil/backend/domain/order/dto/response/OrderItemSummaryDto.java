package com.ongil.backend.domain.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 상품 요약 정보")
public record OrderItemSummaryDto(
	@Schema(description = "상품 ID")
	Long productId,

	@Schema(description = "상품명")
	String productName,

	@Schema(description = "상품 이미지 URL")
	String productImage,

	@Schema(description = "브랜드명")
	String brandName,

	@Schema(description = "선택한 사이즈")
	String selectedSize,

	@Schema(description = "선택한 색상")
	String selectedColor,

	@Schema(description = "수량")
	Integer quantity,

	@Schema(description = "주문 당시 가격")
	Integer price
) {
}
