package com.ongil.backend.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UserUpdateBodyInfoRequest(
        @Schema(description = "키 (cm)", example = "165")
        @Min(value = 100, message = "키는 100cm 이상이어야 합니다.")
        @Max(value = 250, message = "키는 250cm 이하여야 합니다.")
        Integer height,

        @Schema(description = "몸무게 (kg)", example = "60")
        @Min(value = 30, message = "몸무게는 30kg 이상이어야 합니다.")
        @Max(value = 200, message = "몸무게는 200kg 이하여야 합니다.")
        Integer weight,

        @Schema(description = "평소 착용 상의 사이즈", example = "M")
        String usualTopSize,

        @Schema(description = "평소 착용 하의 사이즈", example = "L")
        String usualBottomSize,

        @Schema(description = "평소 착용 신발 사이즈", example = "245")
        String usualShoeSize
) {
}
