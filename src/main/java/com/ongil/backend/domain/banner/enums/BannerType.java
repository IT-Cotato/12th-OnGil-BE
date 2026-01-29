package com.ongil.backend.domain.banner.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BannerType {
	MAGAZINE("매거진 유도"),
	REVIEW_PROMPT("구매 직후 리뷰 작성 유도"),
	MONTHLY_REVIEW_PROMPT("한달 후 리뷰 작성 유도");

	private final String description;
}