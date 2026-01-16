package com.ongil.backend.domain.navigation.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NavigationType {
	HOME("홈"),
	CATEGORY("카테고리"),
	CART("장바구니"),
	WISHLIST("위시리스트"),
	MY_PAGE("마이페이지");

	private final String description;
}
