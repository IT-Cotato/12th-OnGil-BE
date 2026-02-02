package com.ongil.backend.domain.product.dto.response;

import com.ongil.backend.domain.product.enums.ProductType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "추천 상품 응답")
public class RecommendedProductResponse {

	@Schema(description = "상품 ID")
	private Long id;

	@Schema(description = "상품명")
	private String name;

	@Schema(description = "정가")
	private Integer price;

	@Schema(description = "할인율 (%)")
	private Integer discountRate;

	@Schema(description = "최종 가격 (할인 적용된 실제 결제 금액)")
	private Integer finalPrice;

	@Schema(description = "대표 이미지 URL")
	private String thumbnailImageUrl;

	@Schema(description = "브랜드명")
	private String brandName;

	@Schema(description = "상품 타입")
	private ProductType productType;

	@Schema(description = "조회수")
	private Integer viewCount;

	@Schema(description = "장바구니 담긴 횟수")
	private Integer cartCount;

	@Schema(description = "리뷰 평균 평점")
	private Double reviewRating;
}
