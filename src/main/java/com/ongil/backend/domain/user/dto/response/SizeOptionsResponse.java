package com.ongil.backend.domain.user.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "사이즈 옵션 목록 응답")
public class SizeOptionsResponse {

	@Schema(description = "상의 사이즈 목록")
	private List<String> topSizes;

	@Schema(description = "하의 사이즈 목록")
	private List<String> bottomSizes;

	@Schema(description = "신발 사이즈 목록")
	private List<String> shoeSizes;
}