package com.ongil.backend.domain.pricealert.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ongil.backend.domain.pricealert.dto.request.PriceAlertRequest;
import jakarta.validation.Valid;
import com.ongil.backend.domain.pricealert.dto.response.PriceAlertResponse;
import com.ongil.backend.domain.pricealert.service.PriceAlertService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Price Alert", description = "할인 알림 API")
@RestController
@RequestMapping("/api/price-alerts")
@RequiredArgsConstructor
public class PriceAlertController {

	private final PriceAlertService priceAlertService;

	// 할인 알림 설정 & 재설정
	@Operation(summary = "할인 알림 설정", description = "기존 알림이 있으면 자동으로 재설정됨")
	@PostMapping
	public DataResponse<Void> createPriceAlert(
		@AuthenticationPrincipal Long userId,
		@RequestBody @Valid PriceAlertRequest request) {

		priceAlertService.createOrUpdatePriceAlert(userId, request);
		return DataResponse.ok();
	}

	// 현재 활성 중인 알림 조회
	@Operation(summary = "할인 알림 조회", description = "상품 상세 화면 진입 시 기존 설정 조회")
	@GetMapping("/{productId}")
	public DataResponse<PriceAlertResponse> getPriceAlert(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long productId) {

		PriceAlertResponse response = priceAlertService.getPriceAlert(userId, productId);
		return DataResponse.from(response);
	}
}