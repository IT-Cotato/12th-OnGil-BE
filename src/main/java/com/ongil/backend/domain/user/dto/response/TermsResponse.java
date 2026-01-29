package com.ongil.backend.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "약관 내용 응답")
public class TermsResponse {

	@Schema(description = "약관 제목")
	private String title;

	@Schema(description = "약관 내용")
	private String content;

	@Schema(description = "약관 버전")
	private String version;

	@Schema(description = "시행일")
	private String effectiveDate;
}