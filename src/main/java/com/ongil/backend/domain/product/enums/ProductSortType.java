package com.ongil.backend.domain.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductSortType {
	POPULAR("인기순"),
	REVIEW("리뷰 많은 순"),
	PRICE_HIGH("높은 가격순"),
	PRICE_LOW("낮은 가격순");

	private final String description;
}