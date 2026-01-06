package com.ongil.backend.domain.magazine.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MagazineCategory {
	BODY_TYPE("체형"),
	COLOR("색상"),
	MATERIAL("소재"),
	PRICE("가격");

	private final String description;
}
