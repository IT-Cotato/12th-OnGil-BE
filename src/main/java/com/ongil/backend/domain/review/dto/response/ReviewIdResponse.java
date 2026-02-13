package com.ongil.backend.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewIdResponse {
	@Schema(description = "발급된 리뷰 ID", example = "123")
	private Long reviewId;

	public static ReviewIdResponse from(Long reviewId) {
		return new ReviewIdResponse(reviewId);
	}
}
