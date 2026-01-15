package com.ongil.backend.domain.category.dto.response;

import com.ongil.backend.domain.category.enums.CategoryType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryRandomResponse {

	@Schema(description = "카테고리 ID", example = "1")
	private Long categoryId;

	@Schema(description = "카테고리명", example = "상의")
	private String categoryName;

	@Schema(description = "카테고리 타입", example = "PARENT")
	private CategoryType categoryType;

	@Schema(description = "썸네일 URL (인기 1등 상품 이미지)", example = "https://...")
	private String thumbnailUrl;

	@Schema(description = "표시 순서", example = "1")
	private Integer displayOrder;
}