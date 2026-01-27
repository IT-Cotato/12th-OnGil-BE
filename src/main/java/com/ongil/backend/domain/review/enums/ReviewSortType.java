package com.ongil.backend.domain.review.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewSortType {

	BEST("베스트 후기 순"),
	RATING_HIGH("별점 높은 순"),
	RATING_LOW("별점 낮은 순"),
	RECENT("최신순");

	private final String description;
}
