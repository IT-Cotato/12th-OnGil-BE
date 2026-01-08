package com.ongil.backend.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.auth.dto.request.TokenRefreshReqDto;
import com.ongil.backend.domain.auth.dto.response.AuthResDto;
import com.ongil.backend.domain.auth.dto.response.TokenRefreshResDto;
import com.ongil.backend.domain.auth.service.AuthService;
import com.ongil.backend.domain.auth.service.GoogleLoginService;
import com.ongil.backend.domain.auth.service.KakaoLoginService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;
	private final KakaoLoginService kakaoLoginService;
	private final GoogleLoginService googleLoginService;

	@PostMapping("/oauth/kakao")
	@Operation(summary = "카카오 회원가입/로그인 API", description = "인가코드(code)로 카카오 토큰 교환 후, 우리 서비스 JWT 발급")
	public ResponseEntity<DataResponse<AuthResDto>> kakaoLogin(
		@Valid @RequestParam("code") @NotBlank String code
	) {
		AuthResDto res = kakaoLoginService.kakaoLogin(code);
		return ResponseEntity.ok(DataResponse.from(res));
	}

	@GetMapping("/oauth/google")
	@Operation(summary = "구글 회원가입/로그인 API", description = "인가코드(code)로 구글 토큰 교환 후, 우리 서비스 JWT 발급")
	public ResponseEntity<DataResponse<AuthResDto>> googleLogin(
		@Valid @RequestParam("code") @NotBlank String code
	) {
		AuthResDto res = googleLoginService.googleLogin(code);
		return ResponseEntity.ok(DataResponse.from(res));
	}

	@PostMapping("/token/refresh")
	@Operation(summary = "Access/Refresh Token 재발급 API", description = "만료된 accessToken을 refreshToken을 통해 재발급")
	public ResponseEntity<DataResponse<TokenRefreshResDto>> refresh(
		@Valid @RequestBody TokenRefreshReqDto request
	) {
		TokenRefreshResDto res = authService.refreshAccessToken(request.refreshToken());
		return ResponseEntity.ok(DataResponse.from(res));
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃 API", description = "Redis에 저장된 리프레시 토큰을 삭제하여 로그아웃 처리")
	public ResponseEntity<DataResponse<String>> logout(
		@Parameter(hidden = true) @AuthenticationPrincipal Long userId
	) {
		authService.logout(userId);
		return ResponseEntity.ok(DataResponse.from("로그아웃 되었습니다."));
	}

	@DeleteMapping("/withdraw")
	@Operation(summary = "회원 탈퇴 API", description = "계정 삭제 및 리프레시 토큰 파기")
	public ResponseEntity<DataResponse<String>> withdraw(
		@Parameter(hidden = true) @AuthenticationPrincipal Long userId
	) {
		authService.withdraw(userId);
		return ResponseEntity.ok(DataResponse.from("회원 탈퇴가 완료되었습니다."));
	}
}
