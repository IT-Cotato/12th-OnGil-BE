package com.ongil.backend.domain.review.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.ongil.backend.domain.review.enums.ClothingCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "리뷰 상세 응답")
public class ReviewDetailResponse {

	@Schema(description = "리뷰 ID")
	private Long reviewId;

	@Schema(description = "리뷰 상태 (DRAFT: 작성 중 / COMPLETED: 작성 완료)")
	private String reviewStatus;

	@Schema(description = "리뷰 타입 (INITIAL / ONE_MONTH)")
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

	@Schema(description = "상품 정보")
	private ProductInfo product;

	@Schema(description = "구매 직후 리뷰 - 1차 질문 답변")
	private InitialFirstAnswers initialFirstAnswers;

	@Schema(description = "구매 직후 리뷰 - 2차 질문 답변")
	private InitialSecondAnswers initialSecondAnswers;

	@Schema(description = "한달 후 리뷰 답변")
	private OneMonthAnswers oneMonthAnswers;

	@Schema(description = "작성일")
	private LocalDateTime createdAt;

	@Schema(description = "완료일")
	private LocalDateTime completedAt;

	@Getter
	@Builder
	@Schema(description = "리뷰 작성자 정보")
	public static class ReviewerInfo {

		@Schema(description = "작성자 키 (cm)")
		private Integer height;

		@Schema(description = "작성자 몸무게 (kg)")
		private Integer weight;

		@Schema(description = "작성자 상의 사이즈")
		private String usualTopSize;

		@Schema(description = "작성자 하의 사이즈")
		private String usualBottomSize;

		@Schema(description = "작성자 신발 사이즈")
		private String usualShoeSize;
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
	@Schema(description = "상품 정보")
	public static class ProductInfo {

		@Schema(description = "상품 ID")
		private Long productId;

		@Schema(description = "상품명")
		private String productName;

		@Schema(description = "상위 카테고리")
		private ClothingCategory clothingCategory;

		@Schema(description = "브랜드명")
		private String brandName;

		@Schema(description = "대표 이미지 URL")
		private String thumbnailImageUrl;
	}

	@Getter
	@Builder
	@Schema(description = "구매 직후 리뷰 - 1차 질문 답변 (사이즈/색감/소재)")
	public static class InitialFirstAnswers {

		@Schema(description = "사이즈 답변 enum (TIGHT_IMMEDIATELY / TIGHT_WHEN_MOVING / COMFORTABLE / LOOSE / TOO_BIG_NEED_ALTERATION)")
		private String sizeAnswer;

		@Schema(description = "사이즈 2차 질문 방향 (POSITIVE / NEGATIVE / null)")
		private String sizeSecondaryType;

		@Schema(description = "색감 답변 enum (BRIGHTER_THAN_SCREEN / SAME_AS_SCREEN / DARKER_THAN_SCREEN)")
		private String colorAnswer;

		@Schema(description = "소재 답변 enum (VERY_GOOD / GOOD / NORMAL / BAD / VERY_BAD)")
		private String materialAnswer;

		@Schema(description = "소재 2차 질문 방향 (POSITIVE / NEGATIVE / null")
		private String materialSecondaryType;
	}

	@Getter
	@Builder
	@Schema(description = "구매 직후 리뷰 - 2차 질문 답변 (핏/소재특징)")
	public static class InitialSecondAnswers {

		@Schema(description = "핏 문제 부위 목록")
		private List<String> fitIssueParts;

		@Schema(description = "소재 특징 목록")
		private List<String> materialFeatures;
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
