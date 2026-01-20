package com.ongil.backend.domain.category.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {

	@Schema(description = "카테고리 ID")
	private Long categoryId;

	@Schema(description = "카테고리명")
	private String name;

	@Schema(description = "카테고리 이미지")
	private String iconUrl;

	@Schema(description = "표시 순서")
	private Integer displayOrder;

	@Schema(description = "하위 카테고리 목록")
	private List<SubCategoryResponse> subCategories;
}