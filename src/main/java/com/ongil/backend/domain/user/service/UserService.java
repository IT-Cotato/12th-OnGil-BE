package com.ongil.backend.domain.user.service;

import org.springframework.stereotype.Service;

import com.ongil.backend.domain.user.converter.UserConverter;
import com.ongil.backend.domain.user.dto.response.UserInfoResDto;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public UserInfoResDto getUserInfo(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
		return UserConverter.toUserInfoResDto(user);
	}
}
