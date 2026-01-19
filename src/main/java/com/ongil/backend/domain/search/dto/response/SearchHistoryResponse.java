package com.ongil.backend.domain.search.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "검색 기록 응답")
public class SearchHistoryResponse {

	@Schema(description = "검색 기록 ID", example = "1")
	private Long id;

	@Schema(description = "검색 키워드", example = "나이키")
	private String keyword;

	@Schema(description = "검색 일시", example = "2024-01-19T10:30:00")
	private LocalDateTime searchedAt;
}
