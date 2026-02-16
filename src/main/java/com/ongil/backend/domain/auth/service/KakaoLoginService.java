package com.ongil.backend.domain.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.auth.converter.AuthConverter;
import com.ongil.backend.domain.auth.dto.response.AuthResDto;
import com.ongil.backend.domain.auth.dto.response.KakaoTokenResDto;
import com.ongil.backend.domain.auth.dto.response.KakaoUserInfoResDto;
import com.ongil.backend.domain.auth.entity.LoginType;
import com.ongil.backend.domain.auth.client.kakao.KakaoApiClient;
import com.ongil.backend.domain.auth.client.kakao.KakaoAuthClient;
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
public class KakaoLoginService {

	@Value("${kakao.client-id}")
	private String kakaoclientId;
	@Value("${kakao.client-secret}")
	private String kakaoclientSecret;
	@Value("${kakao.redirect-uri}")
	private String kakaoredirectUri;
	@Value("${jwt.access-expiration-ms}")
	private long accessExpMs;

	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final KakaoAuthClient kakaoAuthClient;
	private final KakaoApiClient kakaoApiClient;
	private final RedisRefreshTokenStore refreshTokenStore;

	public AuthResDto kakaoLogin(String code) {
		// 카카오 Access Token
		String kakaoToken = getKakaoAccessToken(code);

		// 카카오 사용자 정보
		KakaoUserInfoResDto userInfo = getKakaoUserInfo(kakaoToken);

		String socialId = (userInfo.id() != null) ? userInfo.id().toString() : null;

		if (socialId == null || socialId.isBlank()) {
			throw new AppException(ErrorCode.INVALID_SOCIAL_USER_INFO);
		}

		// 신규 유저 확인
		boolean isNewUser = !userRepository.existsByLoginTypeAndLoginId(LoginType.KAKAO, socialId);

		User user = userRepository.findByLoginTypeAndLoginId(LoginType.KAKAO, socialId)
			.orElseGet(() -> {
				User newUser = userRepository.save(
					User.builder()
						.loginType(LoginType.KAKAO)
						.loginId(socialId)
						.email(extractEmail(userInfo))
						.profileImg(extractProfileImg(userInfo))
						.name(extractNickname(userInfo))
						.build()
				);
				newUser.restorePoints(2000);
				return newUser;
			});

		// JWT 발급
		String accessToken = jwtTokenProvider.createAccessToken(user.getId());
		String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

		refreshTokenStore.saveRefreshToken(
			String.valueOf(user.getId()),
			refreshToken,
			jwtTokenProvider.getRefreshTokenExpireTime()
		);

		return AuthConverter.toResponse(user, accessToken, refreshToken, isNewUser, accessExpMs);
	}

	// kakaoAuthClient를 사용하여 카카오로부터 access_token을 발급
	private String getKakaoAccessToken(String code) {
		KakaoTokenResDto token = kakaoAuthClient.getAccessToken(
			"authorization_code",
			kakaoclientId,
			kakaoredirectUri,
			code,
			kakaoclientSecret
		);
		return token.accessToken();
	}

	// 카카오로부터 사용자 프로필 정보 추출
	private KakaoUserInfoResDto getKakaoUserInfo(String accessToken) {
		return kakaoApiClient.getUserInfo("Bearer " + accessToken);
	}

	private String extractNickname(KakaoUserInfoResDto userInfo) {
		return Optional.ofNullable(userInfo.kakaoAccount())
			.map(KakaoUserInfoResDto.KakaoAccount::profile)
			.map(KakaoUserInfoResDto.KakaoAccount.Profile::nickname)
			.orElse("kakao_user_" + userInfo.id());
	}

	private String extractEmail(KakaoUserInfoResDto userInfo) {
		if (userInfo.kakaoAccount() != null && userInfo.kakaoAccount().email() != null) {
			return userInfo.kakaoAccount().email();
		}
		// 이메일이 없을 시 임시 이메일 생성 (디비 제약조건)
		return userInfo.id() + "@kakao.user";
	}

	private String extractProfileImg(KakaoUserInfoResDto userInfo) {
		return Optional.ofNullable(userInfo.kakaoAccount())
			.map(KakaoUserInfoResDto.KakaoAccount::profile)
			.map(KakaoUserInfoResDto.KakaoAccount.Profile::profileImg)
			.orElse(null);
	}
}
