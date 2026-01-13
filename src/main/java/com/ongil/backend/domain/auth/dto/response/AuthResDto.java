package com.ongil.backend.domain.auth.dto.response;

import com.ongil.backend.domain.auth.entity.LoginType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AuthResDto(

	@Schema(description = "유저아이디")
	Long userId,

	@Schema(description = "액세스토큰")
	@NotNull
	String accessToken,

	@Schema(description = "리프레시토큰")
	@NotNull
	String refreshToken,

	@Schema(description = "로그인 타입")
	@NotNull
	LoginType loginType,

	@Schema(description = "회원가입 여부(첫 로그인 여부)")
	@NotNull
	Boolean isNewUser,

	@Schema(description = "프로필 이미지")
	String profileUrl,

	@Schema(description = "이름")
	@NotNull
	String nickName,

	@Schema(description = "액세스 토큰 만료 시간")
	@NotNull
	Integer expires_in
) {
}
