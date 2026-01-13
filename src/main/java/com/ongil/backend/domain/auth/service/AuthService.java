package com.ongil.backend.domain.auth.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.auth.converter.AuthConverter;
import com.ongil.backend.domain.auth.dto.request.LoginReqDto;
import com.ongil.backend.domain.auth.dto.response.AuthResDto;
import com.ongil.backend.domain.auth.dto.response.TokenRefreshResDto;
import com.ongil.backend.domain.auth.entity.LoginType;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.config.redis.RedisRefreshTokenStore;
import com.ongil.backend.global.security.jwt.JwtTokenProvider;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final JwtTokenProvider jwtTokenProvider;
	private final RedisRefreshTokenStore refreshTokenStore;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${jwt.access-expiration-ms}")
	private long accessExpMs;

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

	@Transactional
	public AuthResDto login(LoginReqDto loginReqDto) {

		Optional<User> userOptional = userRepository.findByLoginTypeAndLoginId(LoginType.GENERAL, loginReqDto.loginId());

		User user;
		boolean isNewUser = userOptional.isEmpty();
		if (isNewUser) {
			user = userRepository.save(
				User.builder()
					.loginType(LoginType.GENERAL)
					.loginId(loginReqDto.loginId())
					.password(passwordEncoder.encode(loginReqDto.password()))
					.name("일반 로그인 회원")
					.email(loginReqDto.loginId() + "@test.com")
					.build()
			);
		} else {
			// 아이디 존재 시 패스워드 일치 확인
			user = userOptional.get();
			if (!passwordEncoder.matches(loginReqDto.password(), user.getPassword())) {
				throw new AppException(ErrorCode.INVALID_PASSWORD);
			}
		}

		String accessToken = jwtTokenProvider.createAccessToken(user.getId());
		String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

		refreshTokenStore.saveRefreshToken(
			String.valueOf(user.getId()),
			refreshToken,
			jwtTokenProvider.getRefreshTokenExpireTime()
		);

		return AuthConverter.toResponse(user, accessToken, refreshToken, isNewUser, accessExpMs);
	}

}
