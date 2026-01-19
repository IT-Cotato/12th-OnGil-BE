package com.ongil.backend.domain.product.dto.response;

import java.util.List;

import com.ongil.backend.domain.product.enums.ProductType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "상품 상세 정보 응답")
public class ProductDetailResponse {

	@Schema(description = "상품 ID")
	private Long id;

	@Schema(description = "상품명")
	private String name;

	@Schema(description = "상품 설명")
	private String description;

	// 가격 정보
	@Schema(description = "정가")
	private Integer price;

	@Schema(description = "할인율 (%)")
	private Integer discountRate;

	@Schema(description = "최종 가격 (할인 적용된 실제 결제 금액)")
	private Integer finalPrice;

	// 소재 정보
	@Schema(description = "원본 소재 정보")
	private String materialOriginal;

	@Schema(description = "AI 생성 소재 설명 (장점, 단점, 세탁 방법)")
	private MaterialDescription materialDescription;

	// 옵션 정보
	@Schema(description = "상품 옵션 목록 (사이즈, 색상, 재고 정보 포함)")
	private List<ProductOptionResponse> options;

	// 이미지
	@Schema(description = "상품 이미지 URL 목록")
	private List<String> imageUrls;

	// 브랜드 & 카테고리
	@Schema(description = "브랜드 ID")
	private Long brandId;

	@Schema(description = "브랜드명")
	private String brandName;

	@Schema(description = "카테고리 ID")
	private Long categoryId;

	@Schema(description = "카테고리명")
	private String categoryName;

	// 상태
	@Schema(description = "판매 중 여부")
	private Boolean onSale;

	@Schema(description = "상품 타입 (NORMAL: 일반 상품, SPECIAL_SALE: 특가 상품)")
	private ProductType productType;

	// 소재 설명 내부 클래스
	@Getter
	@Builder
	@Schema(description = "AI 생성 소재 설명")
	public static class MaterialDescription {
		@Schema(description = "소재 장점 목록 (최대 4개)")
		private List<String> advantages;

		@Schema(description = "소재 단점 목록 (최대 4개)")
		private List<String> disadvantages;

		@Schema(description = "세탁 방법 목록 (최대 4개)")
		private List<String> care;
	}
}