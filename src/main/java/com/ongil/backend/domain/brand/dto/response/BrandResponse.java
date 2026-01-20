package com.ongil.backend.domain.brand.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "브랜드 응답")
public class BrandResponse {

	@Schema(description = "브랜드 ID")
	private Long id;

	@Schema(description = "브랜드명")
	private String name;

	@Schema(description = "브랜드 설명")
	private String description;

	@Schema(description = "로고 이미지 URL")
	private String logoImageUrl;
}
