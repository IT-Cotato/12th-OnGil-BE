package com.ongil.backend.domain.magazine.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import jakarta.validation.constraints.NotNull;

@Builder
@Schema(description = "매거진 응답 DTO")
public record MagazineResDto(

	@Schema(description = "매거진 ID")
	@NotNull
	Long id,

	@Schema(description = "매거진 제목")
	@NotNull
	String title,

	@Schema(description = "매거진 내용")
	String content,

	@Schema(description = "카테고리 (가격, 체형, 소재, 색상)")
	@NotNull
	String category,

	@Schema(description = "썸네일 이미지 URL")
	String thumbnailUrl,

	@Schema(description = "원문 뉴스 URL")
	@NotNull
	String originalUrl,

	@Schema(description = "언론사명")
	String press,

	@Schema(description = "발행 시간")
	String publishedAt
) {
}
