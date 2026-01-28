package com.ongil.backend.domain.address.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ongil.backend.domain.address.dto.request.AddressCreateRequest;
import com.ongil.backend.domain.address.dto.request.AddressUpdateRequest;
import com.ongil.backend.domain.address.dto.response.AddressResponse;
import com.ongil.backend.domain.address.service.AddressService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Address", description = "배송지 API (토큰 필요)")
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

	private final AddressService addressService;

	@Operation(summary = "내 배송지 목록 조회", description = "로그인한 사용자의 배송지 목록을 조회합니다.")
	@GetMapping
	public DataResponse<List<AddressResponse>> getMyAddresses(
		@AuthenticationPrincipal Long userId
	) {
		List<AddressResponse> addresses = addressService.getMyAddresses(userId);
		return DataResponse.from(addresses);
	}

	@Operation(summary = "특정 배송지 조회", description = "배송지 ID로 특정 배송지를 조회합니다.")
	@GetMapping("/{addressId}")
	public DataResponse<AddressResponse> getAddress(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long addressId
	) {
		AddressResponse address = addressService.getAddress(userId, addressId);
		return DataResponse.from(address);
	}

	@Operation(summary = "기본 배송지 조회", description = "로그인한 사용자의 기본 배송지를 조회합니다.")
	@GetMapping("/default")
	public DataResponse<AddressResponse> getDefaultAddress(
		@AuthenticationPrincipal Long userId
	) {
		AddressResponse address = addressService.getDefaultAddress(userId);
		return DataResponse.from(address);
	}

	@Operation(summary = "배송지 추가", description = "새로운 배송지를 추가합니다.")
	@PostMapping
	public DataResponse<AddressResponse> createAddress(
		@AuthenticationPrincipal Long userId,
		@RequestBody @Valid AddressCreateRequest request
	) {
		AddressResponse response = addressService.createAddress(userId, request);
		return DataResponse.from(response);
	}

	@Operation(summary = "배송지 수정", description = "기존 배송지 정보를 수정합니다.")
	@PutMapping("/{addressId}")
	public DataResponse<AddressResponse> updateAddress(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long addressId,
		@RequestBody @Valid AddressUpdateRequest request
	) {
		AddressResponse response = addressService.updateAddress(userId, addressId, request);
		return DataResponse.from(response);
	}

	@Operation(summary = "기본 배송지 설정", description = "선택한 배송지를 기본 배송지로 설정합니다.")
	@PatchMapping("/{addressId}/default")
	public DataResponse<AddressResponse> setDefaultAddress(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long addressId
	) {
		AddressResponse response = addressService.setDefaultAddress(userId, addressId);
		return DataResponse.from(response);
	}

	@Operation(summary = "배송지 삭제", description = "배송지를 삭제합니다.")
	@DeleteMapping("/{addressId}")
	public DataResponse<String> deleteAddress(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long addressId
	) {
		addressService.deleteAddress(userId, addressId);
		return DataResponse.from("배송지가 삭제되었습니다.");
	}
}
