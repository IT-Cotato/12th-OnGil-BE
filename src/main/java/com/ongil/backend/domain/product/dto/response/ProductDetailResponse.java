package com.ongil.backend.domain.product.dto.response;

import java.util.List;

import com.ongil.backend.domain.product.enums.ProductType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDetailResponse {

	private Long id;
	private String name;
	private String description;

	// 가격 정보
	private Integer originalPrice;
	private Integer discountRate;
	private Integer discountPrice;
	private Integer finalPrice;

	// 소재 정보
	private String materialOriginal;
	private MaterialDescription materialDescription;  // AI 소재 설명 (3개 섹션)
	private String washingMethod;

	// 옵션 정보
	private List<String> sizes;
	private List<String> colors;
	private List<ProductOptionResponse> options;

	// 이미지
	private List<String> imageUrls;

	// 브랜드 & 카테고리
	private Long brandId;
	private String brandName;
	private Long categoryId;
	private String categoryName;

	// 통계
	private Integer viewCount;
	private Integer purchaseCount;

	// 상태
	private Boolean onSale;
	private ProductType productType;
	private String productTypeDescription;

	// 소재 설명 내부 클래스
	@Getter
	@Builder
	public static class MaterialDescription {
		private List<String> advantages;      // 장점 (최대 4개)
		private List<String> disadvantages;   // 단점 (최대 4개)
		private List<String> care;            // 세탁 방법 (최대 4개)
	}
}