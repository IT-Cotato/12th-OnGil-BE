package com.ongil.backend.domain.admin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "브랜드 등록 요청")
public class AdminBrandCreateRequest {

	@NotBlank(message = "브랜드명은 필수입니다.")
	@Schema(description = "브랜드명", example = "나이키")
	private String name;

	@Schema(description = "브랜드 설명", example = "스포츠 의류 브랜드")
	private String description;

	@Schema(description = "로고 이미지 URL", example = "https://example.com/logo.png")
	private String logoImageUrl;
}
