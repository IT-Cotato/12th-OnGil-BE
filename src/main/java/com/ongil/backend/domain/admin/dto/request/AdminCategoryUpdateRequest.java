package com.ongil.backend.domain.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "카테고리 수정 요청")
public class AdminCategoryUpdateRequest {

	@Schema(description = "카테고리명", example = "상의")
	private String name;

	@Schema(description = "아이콘 URL", example = "https://example.com/icon.png")
	private String iconUrl;

	@Schema(description = "정렬 순서", example = "1")
	private Integer displayOrder;

	@Schema(description = "상위 카테고리 ID (하위 카테고리인 경우)", example = "1")
	private Long parentCategoryId;
}
