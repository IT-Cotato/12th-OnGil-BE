package com.ongil.backend.domain.address.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.address.dto.request.ShippingInfoCreateReqDto;
import com.ongil.backend.domain.address.dto.request.ShippingInfoUpdateReqDto;
import com.ongil.backend.domain.address.dto.response.ShippingInfoResDto;
import com.ongil.backend.domain.address.service.AddressService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Address", description = "배송지 API (토큰 필요)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/addresses")
public class AddressController {

	private final AddressService addressService;

	@GetMapping("/me")
	@Operation(summary = "내 배송지 조회", description = "현재 로그인한 사용자의 배송지 정보를 조회합니다.")
	public DataResponse<ShippingInfoResDto> getShippingInfo(
		@AuthenticationPrincipal Long userId
	) {
		ShippingInfoResDto response = addressService.getShippingInfo(userId);
		return DataResponse.from(response);
	}

	@PostMapping
	@Operation(summary = "배송지 등록", description = "새로운 배송지를 등록합니다. 기존 배송지가 있으면 삭제 후 등록됩니다.")
	public DataResponse<ShippingInfoResDto> createShippingInfo(
		@AuthenticationPrincipal Long userId,
		@Valid @RequestBody ShippingInfoCreateReqDto request
	) {
		ShippingInfoResDto response = addressService.createShippingInfo(userId, request);
		return DataResponse.from(response);
	}

	@PatchMapping("/{addressId}")
	@Operation(summary = "배송지 수정", description = "기존 배송지 정보를 수정합니다.")
	public DataResponse<ShippingInfoResDto> updateShippingInfo(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long addressId,
		@Valid @RequestBody ShippingInfoUpdateReqDto request
	) {
		ShippingInfoResDto response = addressService.updateShippingInfo(userId, addressId, request);
		return DataResponse.from(response);
	}
}