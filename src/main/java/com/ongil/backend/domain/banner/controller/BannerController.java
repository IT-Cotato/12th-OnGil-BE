package com.ongil.backend.domain.banner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.banner.dto.response.BannerResponse;
import com.ongil.backend.domain.banner.service.BannerService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Banner", description = "배너 알림 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/banner")
public class BannerController {

	private final BannerService bannerService;

	@GetMapping
	@Operation(summary = "배너 조회 API", description = "토큰 필요. 현재 사용자에게 보여줄 배너를 조회합니다. 구매 직후 리뷰 유도, 한달 후기 유도 우선순위에 따라 반환되며, 해당 배너가 없으면 enabled=false로 반환됩니다.")
	public ResponseEntity<DataResponse<BannerResponse>> getBanner(
		@AuthenticationPrincipal Long userId
	) {
		BannerResponse response = bannerService.getBanner(userId);
		return ResponseEntity.ok(DataResponse.from(response));
	}
}