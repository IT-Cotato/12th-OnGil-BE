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

	@Schema(description = "평소 착용 상의 사이즈", example = "M")
	String usualTopSize,

	@Schema(description = "평소 착용 하의 사이즈", example = "L")
	String usualBottomSize,

	@Schema(description = "평소 착용 신발 사이즈", example = "245")
	String usualShoeSize,

	//Integer -> String으로 변경 (쉼표 포맷팅 적용 위해)
	@Schema(description = "포인트", example = "20,000")
	String points


) {
}


