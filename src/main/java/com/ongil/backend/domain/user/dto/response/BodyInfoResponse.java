package com.ongil.backend.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "체형 정보 응답")
public class BodyInfoResponse {

	@Schema(description = "체형 정보 존재 여부", example = "true")
	private boolean hasBodyInfo;

	@Schema(description = "키 (cm)", example = "175")
	private Integer height;

	@Schema(description = "몸무게 (kg)", example = "70")
	private Integer weight;

	@Schema(description = "상의 사이즈", example = "66")
	private String topSize;

	@Schema(description = "하의 사이즈", example = "30")
	private String bottomSize;

	@Schema(description = "신발 사이즈", example = "270")
	private String shoeSize;
}