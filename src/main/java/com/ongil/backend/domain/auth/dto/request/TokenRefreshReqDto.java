package com.ongil.backend.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record TokenRefreshReqDto(
	@Schema(description = "리프레시토큰")
	@NotNull
	String refreshToken
) {
}
