package com.ongil.backend.domain.review.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "리뷰 목록 응답")
public class ReviewListResponse {

	@Schema(description = "리뷰 ID")
	private Long reviewId;

	@Schema(description = "리뷰 타입")
	private String reviewType;

	@Schema(description = "별점")
	private Integer rating;

	@Schema(description = "도움돼요 수")
	private Integer helpfulCount;

	@Schema(description = "현재 사용자가 도움돼요를 눌렀는지 여부")
	private Boolean isHelpful;

	@Schema(description = "사이즈 관련 후기 문장 목록")
	private List<String> sizeReview;

	@Schema(description = "소재 관련 후기 문장 목록")
	private List<String> materialReview;

	@Schema(description = "기타 텍스트 리뷰")
	private String textReview;

	@Schema(description = "리뷰 이미지 URL 목록")
	private List<String> reviewImageUrls;

	@Schema(description = "작성자 정보")
	private ReviewerInfo reviewer;

	@Schema(description = "구매 옵션 정보")
	private PurchaseOption purchaseOption;

	@Schema(description = "선택지 답변")
	private AnswerSummary answers;

	@Schema(description = "작성일")
	private LocalDateTime createdAt;

	@Getter
	@Builder
	@Schema(description = "리뷰 작성자 정보")
	public static class ReviewerInfo {

		@Schema(description = "작성자 키 (cm)")
		private Integer height;

		@Schema(description = "작성자 몸무게 (kg)")
		private Integer weight;

		@Schema(description = "작성자 평소 사이즈")
		private String usualSize;
	}

	@Getter
	@Builder
	@Schema(description = "구매 옵션 정보")
	public static class PurchaseOption {

		@Schema(description = "선택한 색상")
		private String selectedColor;

		@Schema(description = "선택한 사이즈")
		private String selectedSize;
	}

	@Getter
	@Builder
	@Schema(description = "선택지 답변 요약")
	public static class AnswerSummary {

		@Schema(description = "사이즈 답변")
		private String sizeAnswer;

		@Schema(description = "색감 답변")
		private String colorAnswer;

		@Schema(description = "소재 답변")
		private String materialAnswer;
	}
}
