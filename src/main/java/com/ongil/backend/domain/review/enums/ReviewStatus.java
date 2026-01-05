package com.ongil.backend.domain.review.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReviewStatus {
	DRAFT("작성 중"),
	COMPLETED("작성 완료");

	private final String description;
}
