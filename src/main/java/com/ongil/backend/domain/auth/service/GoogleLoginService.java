package com.ongil.backend.domain.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.auth.client.google.GoogleApiClient;
import com.ongil.backend.domain.auth.client.google.GoogleAuthClient;
import com.ongil.backend.domain.auth.converter.AuthConverter;
import com.ongil.backend.domain.auth.dto.response.AuthResDto;
import com.ongil.backend.domain.auth.dto.response.GoogleTokenResDto;
import com.ongil.backend.domain.auth.dto.response.GoogleUserInfoResDto;
import com.ongil.backend.domain.auth.entity.LoginType;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.config.redis.RedisRefreshTokenStore;
import com.ongil.backend.global.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GoogleLoginService {

	@Value("${google.client-id}")
	private String googleclientId;
	@Value("${google.client-secret}")
	private String googleclientSecret;
	@Value("${google.redirect-uri}")
	private String googleredirectUri;
	@Value("${jwt.access-expiration-ms}")
	private long accessExpMs;

	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final GoogleAuthClient googleAuthClient;
	private final GoogleApiClient googleApiClient;
	private final RedisRefreshTokenStore refreshTokenStore;

	public AuthResDto googleLogin(String code) {
		String googleToken = getGoogleAccessToken(code);
		GoogleUserInfoResDto userInfo = getGoogleUserInfo(googleToken);
		String socialId = userInfo.sub();

		if (socialId == null || socialId.isBlank()) {
			throw new AppException(ErrorCode.INVALID_SOCIAL_USER_INFO);
		}

		boolean isNewUser = !userRepository.existsByLoginTypeAndSocialId(LoginType.GOOGLE, socialId);

		User user = userRepository.findByLoginTypeAndSocialId(LoginType.GOOGLE, socialId)
			.orElseGet(() -> userRepository.save(
				User.builder()
					.loginType(LoginType.GOOGLE)
					.socialId(socialId)
					.email(extractEmail(userInfo))
					.profileImg(extractProfileImg(userInfo))
					.name(extractName(userInfo))
					.build()
			));

		String accessToken = jwtTokenProvider.createAccessToken(user.getId());
		String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

		refreshTokenStore.saveRefreshToken(
			String.valueOf(user.getId()),
			refreshToken,
			jwtTokenProvider.getRefreshTokenExpireTime()
		);

		return AuthConverter.toResponse(user, accessToken, refreshToken, isNewUser, accessExpMs);
	}

	private String getGoogleAccessToken(String code) {
		GoogleTokenResDto token = googleAuthClient.getAccessToken(
			"authorization_code",
			googleclientId,
			googleclientSecret,
			googleredirectUri,
			code
		);
		return token.accessToken();
	}

	private GoogleUserInfoResDto getGoogleUserInfo(String accessToken) {
		return googleApiClient.getUserInfo("Bearer " + accessToken);
	}

	private String extractName(GoogleUserInfoResDto userInfo) {
		return Optional.ofNullable(userInfo.name())
			.filter(name -> !name.isBlank())
			.orElse("google_user_" + userInfo.sub());
	}

	private String extractEmail(GoogleUserInfoResDto userInfo) {
		if (userInfo.email() != null && !userInfo.email().isBlank()) {
			return userInfo.email();
		}
		return userInfo.sub() + "@google.user";
	}

	private String extractProfileImg(GoogleUserInfoResDto userInfo) {
		return Optional.ofNullable(userInfo.picture())
			.filter(img -> !img.isBlank())
			.orElse(null);
	}
}