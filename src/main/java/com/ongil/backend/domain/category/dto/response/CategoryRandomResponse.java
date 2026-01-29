package com.ongil.backend.domain.category.dto.response;

import com.ongil.backend.domain.category.enums.CategoryType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryRandomResponse {

	@Schema(description = "카테고리 ID")
	private Long categoryId;

	@Schema(description = "카테고리명")
	private String name;

	@Schema(description = "카테고리 타입")
	private CategoryType categoryType;

	@Schema(description = "썸네일 URL (인기 1등 상품 이미지)")
	private String thumbnailUrl;

	@Schema(description = "표시 순서")
	private Integer displayOrder;
}