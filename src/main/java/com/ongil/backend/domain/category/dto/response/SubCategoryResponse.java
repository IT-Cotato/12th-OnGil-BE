package com.ongil.backend.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubCategoryResponse {

	@Schema(description = "카테고리 ID")
	private Long categoryId;

	@Schema(description = "카테고리명")
	private String name;

	@Schema(description = "카테고리 이미지")
	private String iconUrl;

	@Schema(description = "표시 순서")
	private Integer displayOrder;

	@Schema(description = "상위 카테고리명")
	private String parentCategoryName;
}