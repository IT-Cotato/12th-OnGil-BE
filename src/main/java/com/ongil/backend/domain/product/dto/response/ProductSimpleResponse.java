package com.ongil.backend.domain.product.dto.response;

import com.ongil.backend.domain.product.enums.ProductType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductSimpleResponse {

	private Long id;
	private String name;

	// 가격 정보
	private Integer originalPrice;
	private Integer discountRate;
	private Integer discountPrice;
	private Integer finalPrice;

	// 대표 이미지
	private String thumbnailImageUrl;

	// 브랜드
	private String brandName;

	// 상태
	private ProductType productType;
	private String productTypeDescription;

	// 통계 (정렬용)
	private Integer viewCount;
	private Integer purchaseCount;
	private Integer reviewCount;
}
