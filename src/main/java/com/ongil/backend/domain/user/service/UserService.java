package com.ongil.backend.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.user.converter.UserConverter;
import com.ongil.backend.domain.user.dto.request.UserUpdateBodyInfoRequest;
import com.ongil.backend.domain.user.dto.response.UserInfoResDto;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	// 1. 내 정보 조회
	public UserInfoResDto getUserInfo(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

		return UserConverter.toUserInfoResDto(user);
	}

	// 2. 프로필 이미지 변경
	@Transactional // 쓰기 권한 부여
	public UserInfoResDto updateProfileImage(Long userId, String newImageUrl) {
		User user = findUser(userId);

		user.updateProfileImage(newImageUrl);

		return UserConverter.toUserInfoResDto(user);
	}

	// 3. 체형 정보 변경
	@Transactional // 쓰기 권한 부여
	public UserInfoResDto updateBodyInfo(Long userId, UserUpdateBodyInfoRequest request) {
		User user = findUser(userId);

		user.updateBodyInfo(
			request.height(),
			request.weight(),
			request.usualTopSize(),
			request.usualBottomSize(),
			request.usualShoeSize()
		);

		return UserConverter.toUserInfoResDto(user);
	}

	// 공통 유저 찾기 메서드
	private User findUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
	}
}
