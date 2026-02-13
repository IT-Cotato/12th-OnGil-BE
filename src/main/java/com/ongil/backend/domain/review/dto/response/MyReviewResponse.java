package com.ongil.backend.domain.review.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "내가 작성한 리뷰 응답")
public class MyReviewResponse {

	@Schema(description = "리뷰 ID")
	private Long reviewId;

	@Schema(description = "리뷰 타입 (INITIAL / ONE_MONTH)")
	private String reviewType;

	@Schema(description = "별점")
	private Integer rating;

	@Schema(description = "도움돼요 수")
	private Integer helpfulCount;

	@Schema(description = "사이즈 관련 후기 문장 목록")
	private List<String> sizeReview;

	@Schema(description = "소재 관련 후기 문장 목록")
	private List<String> materialReview;

	@Schema(description = "기타 텍스트 리뷰")
	private String textReview;

	@Schema(description = "리뷰 이미지 URL 목록")
	private List<String> reviewImageUrls;

	@Schema(description = "상품 정보")
	private ProductInfo product;

	@Schema(description = "구매 옵션")
	private String purchaseOption;

	@Schema(description = "선택지 답변 요약 (일반 리뷰)")
	private AnswerSummary answers;

	@Schema(description = "한달 후 리뷰 답변")
	private OneMonthAnswers oneMonthAnswers;

	@Schema(description = "획득 포인트")
	private Integer earnedPoints;

	@Schema(description = "작성일")
	private LocalDateTime createdAt;

	@Getter
	@Builder
	@Schema(description = "상품 정보")
	public static class ProductInfo {

		@Schema(description = "상품 ID")
		private Long productId;

		@Schema(description = "상품명")
		private String productName;

		@Schema(description = "브랜드명")
		private String brandName;

		@Schema(description = "대표 이미지 URL")
		private String thumbnailImageUrl;
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

		@Schema(description = "핏 문제 부위")
		private String fitIssueParts;

		@Schema(description = "소재 특징")
		private String materialFeatures;
	}

	@Getter
	@Builder
	@Schema(description = "한달 후 리뷰 답변")
	public static class OneMonthAnswers {

		@Schema(description = "전체 평가")
		private String overall;

		@Schema(description = "변화 항목")
		private String changes;
	}
}
