package com.ongil.backend.domain.address.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.address.dto.request.ShippingInfoCreateReqDto;
import com.ongil.backend.domain.address.dto.request.ShippingInfoUpdateReqDto;
import com.ongil.backend.domain.address.dto.response.AddressListResponse;
import com.ongil.backend.domain.address.dto.response.ShippingInfoResDto;
import com.ongil.backend.domain.address.service.AddressService;
import com.ongil.backend.global.common.dto.DataResponse;

import java.util.List;

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

	@GetMapping
	@Operation(summary = "내 배송지 목록 조회", description = "현재 로그인한 사용자의 전체 배송지 목록을 조회합니다. 토큰 필요")
	public DataResponse<List<AddressListResponse>> getAddressList(
		@AuthenticationPrincipal Long userId
	) {
		return DataResponse.from(addressService.getAddressList(userId));
	}

	@GetMapping("/me")
	@Operation(summary = "내 배송지 조회", description = "현재 로그인한 사용자의 배송지 정보를 조회합니다.")
	public DataResponse<ShippingInfoResDto> getShippingInfo(
		@AuthenticationPrincipal Long userId
	) {
		ShippingInfoResDto response = addressService.getShippingInfo(userId);
		return DataResponse.from(response);
	}

	@PostMapping
	@Operation(summary = "배송지 등록", description = "새로운 배송지를 등록합니다. 첫 번째 배송지는 자동으로 기본 배송지로 설정됩니다.")
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

	@PatchMapping("/{addressId}/default")
	@Operation(summary = "기본 배송지 설정", description = "선택한 배송지를 기본 배송지로 설정합니다.")
	public DataResponse<String> setDefaultAddress(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long addressId
	) {
		addressService.setDefaultAddress(userId, addressId);
		return DataResponse.from("기본 배송지로 설정되었습니다.");
	}

	@DeleteMapping("/{addressId}")
	@Operation(summary = "배송지 삭제", description = "배송지를 삭제합니다. 기본 배송지를 삭제하면 다른 주소가 자동으로 기본 배송지로 설정됩니다.")
	public DataResponse<String> deleteAddress(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long addressId
	) {
		addressService.deleteAddress(userId, addressId);
		return DataResponse.from("배송지가 삭제되었습니다.");
	}
}