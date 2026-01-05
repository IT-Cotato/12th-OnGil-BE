package com.ongil.backend.domain.review.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewType {
	INITIAL("초기 리뷰"),
	ONE_MONTH("한달 후기");

	private final String description;
}