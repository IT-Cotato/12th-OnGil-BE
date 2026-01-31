package com.ongil.backend.domain.admin.dto.request;

import com.ongil.backend.domain.product.enums.ProductType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "상품 등록 요청")
public class AdminProductCreateRequest {

	@NotBlank(message = "상품명은 필수입니다.")
	@Schema(description = "상품명", example = "베이직 코튼 티셔츠")
	private String name;

	@Schema(description = "상품 설명", example = "편안한 착용감의 코튼 티셔츠입니다.")
	private String description;

	@NotNull(message = "가격은 필수입니다.")
	@Schema(description = "가격", example = "29000")
	private Integer price;

	@Schema(description = "소재 정보", example = "면 100%")
	private String materialOriginal;

	@Schema(description = "이미지 URL들 (쉼표로 구분)", example = "https://example.com/img1.jpg,https://example.com/img2.jpg")
	private String imageUrls;

	@Schema(description = "사이즈들 (쉼표로 구분)", example = "S,M,L,XL")
	private String sizes;

	@Schema(description = "색상들 (쉼표로 구분)", example = "화이트,블랙,네이비")
	private String colors;

	@Schema(description = "할인율 (%)", example = "10")
	private Integer discountRate;

	@Schema(description = "상품 타입", example = "NORMAL")
	private ProductType productType = ProductType.NORMAL;

	@NotNull(message = "브랜드 ID는 필수입니다.")
	@Schema(description = "브랜드 ID", example = "1")
	private Long brandId;

	@NotNull(message = "카테고리 ID는 필수입니다.")
	@Schema(description = "카테고리 ID", example = "1")
	private Long categoryId;
}
