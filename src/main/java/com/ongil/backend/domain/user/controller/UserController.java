package com.ongil.backend.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.user.dto.request.UserUpdateBodyInfoRequest;
import com.ongil.backend.domain.user.dto.request.UserUpdateProfileRequest;

import com.ongil.backend.domain.user.dto.response.UserInfoResDto;
import com.ongil.backend.domain.user.service.UserService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@GetMapping("/me")
	@Operation(summary = "내 정보 조회 API", description = "현재 로그인한 사용자 정보를 조회")
	public ResponseEntity<DataResponse<UserInfoResDto>> getMyInfo(
		@AuthenticationPrincipal Long userId
	) {
		UserInfoResDto res = userService.getUserInfo(userId);
		return ResponseEntity.ok(DataResponse.from(res));
	}

	@GetMapping("/{userId}")
	@Operation(summary = "특정 사용자 정보 조회 API", description = "ID를 통해 특정 사용자의 정보를 조회")
	public ResponseEntity<DataResponse<UserInfoResDto>> getUserInfo(
		@PathVariable(name = "userId") Long userId
	) {
		UserInfoResDto res = userService.getUserInfo(userId);
		return ResponseEntity.ok(DataResponse.from(res));
	}

	@PatchMapping("/me/profile-image")
	@Operation(summary = "프로필 이미지 수정 API", description = "현재 로그인한 사용자의 프로필 이미지를 수정")
	public ResponseEntity<DataResponse<UserInfoResDto>> updateProfileImage(
		@AuthenticationPrincipal Long userId,
		@RequestBody UserUpdateProfileRequest request
	) {
		UserInfoResDto res = userService.updateProfileImage(userId, request.profileImageUrl());
		return ResponseEntity.ok(DataResponse.from(res));
	}

	@PatchMapping("/me/body-info")
	@Operation(summary = "체형 정보 수정 API", description = "현재 로그인한 사용자의 체형 정보를 수정")
	public ResponseEntity<DataResponse<UserInfoResDto>> updateBodyInfo(
		@AuthenticationPrincipal Long userId,
		@RequestBody @Validated UserUpdateBodyInfoRequest request
	) {
		UserInfoResDto res = userService.updateBodyInfo(userId, request);
		return ResponseEntity.ok(DataResponse.from(res));
	}

}
