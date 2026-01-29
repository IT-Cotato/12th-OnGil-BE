package com.ongil.backend.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "미작성 리뷰 개수 응답")
public class PendingReviewCountResponse {

	@Schema(description = "미작성 리뷰 개수", example = "3")
	private Integer pendingReviewCount;
}