package com.ongil.backend.domain.user.dto.response;

import com.ongil.backend.domain.auth.entity.LoginType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UserInfoResDto(

	@Schema(description = "유저 ID")
	Long userId,

	@Schema(description = "이름")
	String name,

	@Schema(description = "회원가입 경로")
	LoginType loginType,

	@Schema(description = "프로필 이미지 URL")
	String profileUrl,

	@Schema(description = "핸드폰 번호")
	String phone,

	@Schema(description = "키")
	Integer height,

	@Schema(description = "몸무게")
	Integer weight,

	@Schema(description = "평소 착용 사이즈")
	String usualSize,

	@Schema(description = "포인트")
	Integer points
) {}


