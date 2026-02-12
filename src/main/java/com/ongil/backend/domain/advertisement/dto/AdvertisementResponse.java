package com.ongil.backend.domain.advertisement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "할인 광고 응답")
public record AdvertisementResponse(

	@Schema(description = "광고 ID")
	Long id,

	@Schema(description = "광고 이미지 URL")
	String imageUrl,

	@Schema(description = "광고 제목")
	String title,

	@Schema(description = "광고 문구")
	String description
) {
}