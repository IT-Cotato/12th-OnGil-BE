package com.ongil.backend.domain.review.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "리뷰 통계 요약 응답")
public class ReviewSummaryResponse {

	@Schema(description = "평균 별점")
	private Double averageRating;

	@Schema(description = "일반 리뷰 개수")
	private Long initialReviewCount;

	@Schema(description = "한달 후 리뷰 개수")
	private Long oneMonthReviewCount;

	@Schema(description = "사이즈 부문 통계 (유사 체형 기준)")
	private CategorySummary sizeSummary;

	@Schema(description = "색감 부문 통계 (전체 유저 기준)")
	private CategorySummary colorSummary;

	@Schema(description = "소재 부문 통계 (전체 유저 기준)")
	private CategorySummary materialSummary;

	@Getter
	@Builder
	@Schema(description = "부문별 통계")
	public static class CategorySummary {

		@Schema(description = "부문명")
		private String category;

		@Schema(description = "총 응답 수")
		private Long totalCount;

		@Schema(description = "가장 많이 선택된 답변")
		private String topAnswer;

		@Schema(description = "가장 많이 선택된 답변의 응답 수")
		private Long topAnswerCount;

		@Schema(description = "선택지별 통계 목록")
		private List<AnswerStat> answerStats;
	}

	@Getter
	@Builder
	@Schema(description = "선택지별 통계")
	public static class AnswerStat {

		@Schema(description = "선택지 답변")
		private String answer;

		@Schema(description = "선택 횟수")
		private Long count;

		@Schema(description = "선택 비율 (%)")
		private Double percentage;
	}
}
