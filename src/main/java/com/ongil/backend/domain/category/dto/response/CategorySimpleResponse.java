package com.ongil.backend.domain.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategorySimpleResponse {

	@Schema(description = "카테고리 ID", example = "12")
	private Long categoryId;

	@Schema(description = "카테고리명", example = "니트")
	private String categoryName;

	@Schema(description = "아이콘 URL", example = "https://...")
	private String iconUrl;

	@Schema(description = "표시 순서", example = "1")
	private Integer displayOrder;
}