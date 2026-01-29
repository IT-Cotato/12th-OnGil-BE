package com.ongil.backend.domain.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "상품 검색 조건")
public class ProductSearchCondition {

	@Schema(description = "카테고리 ID", example = "1")
	private Long categoryId;

	@Schema(description = "브랜드 ID", example = "5")
	private Long brandId;

	@Schema(description = "가격 범위 (형식: minPrice-maxPrice)", example = "50000-100000")
	private String priceRange;

	@Schema(description = "사이즈 (예: XS, S, M, L, XL)", example = "M")
	private String size;

	// 가격 범위 파싱
	public Integer[] parsePriceRange() {
		if (priceRange == null || priceRange.trim().isEmpty()) {
			return null;
		}

		try {
			String[] parts = priceRange.split("-");
			if (parts.length != 2) {
				return null;
			}

			Integer minPrice = Integer.parseInt(parts[0].trim());
			Integer maxPrice = Integer.parseInt(parts[1].trim());

			if (minPrice < 0 || maxPrice < 0) {
				return null;
			}

			if (minPrice > maxPrice) {
				return null;
			}

			return new Integer[] {minPrice, maxPrice};

		} catch (NumberFormatException e) {
			return null;
		}
	}
}