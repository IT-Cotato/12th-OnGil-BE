package com.ongil.backend.domain.product.dto.response;

import com.ongil.backend.domain.product.entity.ProductOption;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "상품 옵션 응답")
public class ProductOptionResponse {

	@Schema(description = "옵션 ID")
	private Long optionId;

	@Schema(description = "사이즈")
	private String size;

	@Schema(description = "색상")
	private String color;

	@Schema(description = "재고 수량 (실시간 재고 표시용)")
	private Integer stock;

	@Schema(description = "재고 상태 (AVAILABLE: 구매 가능, SOLD_OUT: 품절)")
	private ProductOption.StockStatus stockStatus;
}