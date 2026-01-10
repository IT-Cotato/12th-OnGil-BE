package com.ongil.backend.product.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductSearchCondition {

	private Long categoryId;
	private Long brandId;
	private String priceRange;
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
