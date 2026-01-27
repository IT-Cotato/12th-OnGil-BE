package com.ongil.backend.domain.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "리뷰 도움돼요 토글 응답")
public class ReviewHelpfulResponse {

	@Schema(description = "리뷰 ID")
	private Long reviewId;

	@Schema(description = "현재 도움돼요 상태 (true: 눌림, false: 해제)")
	private Boolean isHelpful;

	@Schema(description = "현재 도움돼요 수")
	private Integer helpfulCount;
}
