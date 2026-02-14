package com.ongil.backend.domain.category.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SizeChartType {
	TOP_OUTERWEAR("상의 및 아우터"),
	PANTS("바지"),
	SKIRT("스커트"),
	DRESS("원피스");

	private final String description;
}
