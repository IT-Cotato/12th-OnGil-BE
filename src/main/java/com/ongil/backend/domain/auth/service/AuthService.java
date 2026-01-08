package com.ongil.backend.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.auth.dto.response.TokenRefreshResDto;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.config.redis.RedisRefreshTokenStore;
import com.ongil.backend.global.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisRefreshTokenStore refreshTokenStore;
	private final UserRepository userRepository;

	public TokenRefreshResDto refreshAccessToken(String refreshTokenValue) {

		if (!jwtTokenProvider.validateRefreshToken(refreshTokenValue)) {
			throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
		}

		Long userId = jwtTokenProvider.getUserId(refreshTokenValue);
		String savedToken = refreshTokenStore.getRefreshToken(String.valueOf(userId));

		if (savedToken == null || !savedToken.equals(refreshTokenValue)) {
			refreshTokenStore.removeRefreshToken(String.valueOf(userId));
			throw new AppException(ErrorCode.STOLEN_REFRESH_TOKEN);
		}

		// 새 토큰 세트 생성
		String newAccessToken = jwtTokenProvider.createAccessToken(userId);
		String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

		// Redis 업데이트
		refreshTokenStore.saveRefreshToken(String.valueOf(userId), newRefreshToken, jwtTokenProvider.getRefreshTokenExpireTime());

		return new TokenRefreshResDto(newAccessToken, newRefreshToken);
	}

	@Transactional
	public void logout(Long userId) {
		refreshTokenStore.removeRefreshToken(String.valueOf(userId));
	}

	@Transactional
	public void withdraw(Long userId) {
		refreshTokenStore.removeRefreshToken(String.valueOf(userId));
		userRepository.deleteById(userId);
	}
}
