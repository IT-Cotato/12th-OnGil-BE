package com.ongil.backend.domain.product.dto.response;

import com.ongil.backend.domain.product.enums.ProductType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "상품 간단 정보 응답")
public class ProductSimpleResponse {

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

	@Schema(description = "상품 타입 (NORMAL: 일반 상품, SPECIAL_SALE: 특가 상품)")
	private ProductType productType;

	@Schema(description = "조회수")
	private Integer viewCount;

	@Schema(description = "구매 횟수")
	private Integer purchaseCount;

	@Schema(description = "리뷰 개수")
	private Integer reviewCount;
}