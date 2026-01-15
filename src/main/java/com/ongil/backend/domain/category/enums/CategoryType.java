package com.ongil.backend.domain.category.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CategoryType {
	PARENT("상위 카테고리"),
	CHILD("하위 카테고리");

	private final String description;
}
