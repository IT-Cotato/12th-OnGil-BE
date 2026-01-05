package com.ongil.backend.domain.product.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductType {
	NORMAL("일반 상품"),
	SPECIAL_SALE("특가 상품");

	private final String description;
}
