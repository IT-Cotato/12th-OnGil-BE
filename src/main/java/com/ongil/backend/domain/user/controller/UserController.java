package com.ongil.backend.domain.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ongil.backend.domain.user.dto.request.BodyInfoRequest;
import com.ongil.backend.domain.user.dto.request.UserUpdateProfileRequest;
import com.ongil.backend.domain.user.dto.response.BodyInfoResponse;
import com.ongil.backend.domain.user.dto.response.SizeOptionsResponse;
import com.ongil.backend.domain.user.dto.response.TermsResponse;
import com.ongil.backend.domain.user.dto.response.UserInfoResDto;
import com.ongil.backend.domain.user.service.UserService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "User", description = "회원 API")
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
	@Operation(summary = "프로필 이미지 수정 API (URL 방식)", description = "현재 로그인한 사용자의 프로필 이미지를 URL로 수정")
	public ResponseEntity<DataResponse<UserInfoResDto>> updateProfileImage(
		@AuthenticationPrincipal Long userId,
		@RequestBody UserUpdateProfileRequest request
	) {
		UserInfoResDto res = userService.updateProfileImage(userId, request.profileImageUrl());
		return ResponseEntity.ok(DataResponse.from(res));
	}

	@PostMapping("/me/profile-image/upload")
	@Operation(summary = "프로필 이미지 업로드 API", description = "현재 로그인한 사용자의 프로필 이미지를 파일로 업로드 (multipart/form-data)")
	public ResponseEntity<DataResponse<UserInfoResDto>> uploadProfileImage(
		@AuthenticationPrincipal Long userId,
		@RequestParam("file") MultipartFile file
	) {
		UserInfoResDto res = userService.uploadProfileImage(userId, file);
		return ResponseEntity.ok(DataResponse.from(res));
	}

	@GetMapping("/me/body-info")
	@Operation(summary = "체형 정보 조회 API", description = "로그인한 회원의 체형 정보를 조회합니다. 5개 항목 중 하나라도 없으면 hasBodyInfo=false (토큰 필요)")
	public ResponseEntity<DataResponse<BodyInfoResponse>> getBodyInfo(
		@AuthenticationPrincipal Long userId
	) {
		BodyInfoResponse res = userService.getBodyInfo(userId);
		return ResponseEntity.ok(DataResponse.from(res));
	}

	@PutMapping("/me/body-info")
	@Operation(summary = "체형 정보 저장/수정 API", description = "로그인한 회원의 체형 정보를 저장하거나 수정합니다. 모든 항목 필수입니다. (토큰 필요)")
	public ResponseEntity<DataResponse<BodyInfoResponse>> updateBodyInfo(
		@AuthenticationPrincipal Long userId,
		@Valid @RequestBody BodyInfoRequest request
	) {
		BodyInfoResponse res = userService.updateBodyInfo(userId, request);
		return ResponseEntity.ok(DataResponse.from(res));
	}

	@GetMapping("/body-info/size-options")
	@Operation(summary = "사이즈 옵션 목록 조회 API", description = "드롭다운에 표시할 사이즈 옵션 목록을 조회합니다.")
	public ResponseEntity<DataResponse<SizeOptionsResponse>> getSizeOptions() {
		SizeOptionsResponse res = userService.getSizeOptions();
		return ResponseEntity.ok(DataResponse.from(res));
	}

	@GetMapping("/body-info/terms")
	@Operation(summary = "수집·이용 동의 약관 조회 API", description = "사이즈 정보 수집 동의 약관을 조회합니다.")
	public ResponseEntity<DataResponse<TermsResponse>> getBodyInfoTerms() {
		TermsResponse res = userService.getBodyInfoTerms();
		return ResponseEntity.ok(DataResponse.from(res));
	}

}
