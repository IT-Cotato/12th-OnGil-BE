package com.ongil.backend.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "체형 정보 저장 요청")
public record BodyInfoRequest(

	@NotNull(message = "키를 입력해주세요.")
	@Min(value = 100, message = "키는 100cm 이상이어야 합니다.")
	@Max(value = 250, message = "키는 250cm 이하여야 합니다.")
	@Schema(description = "키 (cm)", example = "175")
	Integer height,

	@NotNull(message = "몸무게를 입력해주세요.")
	@Min(value = 20, message = "몸무게는 20kg 이상이어야 합니다.")
	@Max(value = 300, message = "몸무게는 300kg 이하여야 합니다.")
	@Schema(description = "몸무게 (kg)", example = "70")
	Integer weight,

	@NotBlank(message = "상의 사이즈를 선택해주세요.")
	@Schema(description = "상의 사이즈", example = "66")
	String topSize,

	@NotBlank(message = "하의 사이즈를 선택해주세요.")
	@Schema(description = "하의 사이즈", example = "30")
	String bottomSize,

	@NotBlank(message = "신발 사이즈를 선택해주세요.")
	@Schema(description = "신발 사이즈", example = "270")
	String shoeSize,

	@NotNull(message = "수집 동의 여부를 선택해주세요.")
	@AssertTrue(message = "사이즈 정보 수집에 동의해야 합니다.")
	@Schema(description = "수집 동의 여부", example = "true")
	Boolean agreedToCollection
) {
}