package com.ongil.backend.domain.review.dto.request;

import com.ongil.backend.domain.review.enums.ReviewSortType;
import com.ongil.backend.domain.review.enums.ReviewType;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewListRequest {

	@Parameter(description = "리뷰 타입")
	private ReviewType reviewType = ReviewType.INITIAL;

	@Parameter(description = "사이즈 필터")
	private String size;

	@Parameter(description = "색상 필터")
	private String color;

	@Parameter(description = "정렬 기준")
	private ReviewSortType sort = ReviewSortType.BEST;

	@Parameter(description = "내 체형만 보기")
	private boolean mySizeOnly = true;

	@Parameter(description = "페이지 번호")
	private int page = 0;

	@Parameter(description = "페이지 크기")
	private int pageSize = 10;
}
