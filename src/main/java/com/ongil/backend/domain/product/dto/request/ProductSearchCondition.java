package com.ongil.backend.domain.product.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "상품 검색 조건")
public class ProductSearchCondition {

	@Schema(description = "카테고리 ID", example = "1")
	private Long categoryId;

	@Schema(description = "브랜드 ID 목록 (다중 선택)", example = "[1, 2, 3]")
	private List<Long> brandIds;

	@Schema(description = "가격 범위 (형식: minPrice-maxPrice)", example = "50000-100000")
	private String priceRange;

	@Schema(description = "사이즈 목록 (다중 선택, 예: XS, S, M, L, XL)", example = "[M, L]")
	private List<String> sizes;

	// 사이즈 목록을 REGEXP 패턴으로 변환 (예: [M, L] → "(^|,)(M|L)(,|$)")
	public String buildSizesPattern() {
		if (sizes == null || sizes.isEmpty()) {
			return null;
		}
		String sizeGroup = String.join("|", sizes);
		return "(^|,)(" + sizeGroup + ")(,|$)";
	}

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