package com.ongil.backend.domain.auth.dto.response;

public record TokenRefreshResDto(
	String accessToken,
	String refreshToken
) {
}
