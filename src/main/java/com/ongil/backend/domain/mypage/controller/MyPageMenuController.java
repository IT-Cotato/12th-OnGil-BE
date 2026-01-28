package com.ongil.backend.domain.mypage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.mypage.dto.response.MyPageMenuResponse;
import com.ongil.backend.domain.mypage.service.MyPageMenuService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "MyPage", description = "마이페이지 API (토큰 필요)")
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageMenuController {

	private final MyPageMenuService myPageMenuService;

	@GetMapping("/menu")
	@Operation(summary = "마이페이지 메뉴 조회 API", 
		description = "마이페이지의 메뉴 항목들을 조회합니다. 각 메뉴에는 배지 카운트(장바구니 개수, 찜 개수, 작성 가능한 리뷰 개수 등)가 포함될 수 있습니다.")
	public ResponseEntity<DataResponse<MyPageMenuResponse>> getMyPageMenu(
		@AuthenticationPrincipal Long userId
	) {
		MyPageMenuResponse response = myPageMenuService.getMyPageMenu(userId);
		return ResponseEntity.ok(DataResponse.from(response));
	}
}
