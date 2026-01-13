package com.ongil.backend.domain.auth.converter;

import com.ongil.backend.domain.auth.dto.response.AuthResDto;
import com.ongil.backend.domain.user.entity.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthConverter {

	public static AuthResDto toResponse(User user, String accessToken, String refreshToken,
		boolean isNewUser, long accessExpMs) {
		return AuthResDto.builder()
			.userId(user.getId())
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.loginType(user.getLoginType())
			.isNewUser(isNewUser)
			.profileUrl(user.getProfileImg())
			.nickName(user.getName())
			.expires_in((int) (accessExpMs / 1000)) // 초 단위
			.build();
	}
}
