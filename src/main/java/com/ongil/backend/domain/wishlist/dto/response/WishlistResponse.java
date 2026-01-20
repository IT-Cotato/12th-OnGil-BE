package com.ongil.backend.domain.wishlist.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "찜 목록 응답")
public class WishlistResponse {

	@Schema(description = "찜 ID")
	private Long wishlistId;

	@Schema(description = "상품 ID")
	private Long productId;

	@Schema(description = "상품명")
	private String productName;

	@Schema(description = "브랜드명")
	private String brandName;

	// 가격 정보
	@Schema(description = "정가")
	private Integer price;

	@Schema(description = "할인율 (%)")
	private Integer discountRate;

	@Schema(description = "최종 가격 (할인 적용된 실제 결제 금액)")
	private Integer finalPrice;

	// 이미지
	@Schema(description = "대표 이미지 URL")
	private String thumbnailImageUrl;

	// 카테고리 정보
	@Schema(description = "카테고리 ID")
	private Long categoryId;

	@Schema(description = "카테고리명")
	private String categoryName;
}