package com.ongil.backend.domain.review.dto.response;

import java.time.LocalDateTime;

import com.ongil.backend.domain.review.enums.ClothingCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "작성 가능한 리뷰 응답")
public class PendingReviewResponse {

	@Schema(description = "주문 상품 ID")
	private Long orderItemId;

	@Schema(description = "작성 가능한 리뷰 타입 (INITIAL / ONE_MONTH)")
	private String availableReviewType;
	
	@Schema(description = "작성 중인 리뷰 ID (DRAFT 상태일 때만 존재)")
	private Long reviewId;

	@Schema(description = "작성 중인 리뷰 상태 (DRAFT: 작성 중 / null: 미시작)")
	private String reviewStatus;

	@Schema(description = "상품 정보")
	private ProductInfo product;

	@Schema(description = "구매 옵션")
	private String purchaseOption;

	@Schema(description = "주문 완료일")
	private LocalDateTime orderedAt;

	@Schema(description = "리뷰 작성 가능일")
	private LocalDateTime reviewAvailableAt;

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
}
