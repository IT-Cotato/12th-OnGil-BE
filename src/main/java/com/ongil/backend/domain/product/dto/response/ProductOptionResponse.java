package com.ongil.backend.domain.product.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductOptionResponse {

	private Long optionId;
	private String size;
	private String color;
	private Integer stock;
	private Integer additionalPrice;
	private Boolean available;    // 재고 있음
	private Boolean lowStock;     // 5개 이하
	private Boolean soldOut;      // 품절
}
