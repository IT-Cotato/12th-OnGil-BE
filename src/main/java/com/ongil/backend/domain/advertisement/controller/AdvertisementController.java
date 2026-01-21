package com.ongil.backend.domain.advertisement.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ongil.backend.domain.advertisement.dto.request.AdvertisementCreateRequest;
import com.ongil.backend.domain.advertisement.dto.request.AdvertisementUpdateRequest;
import com.ongil.backend.domain.advertisement.dto.request.UserAdPreferenceRequest;
import com.ongil.backend.domain.advertisement.dto.response.AdvertisementResponse;
import com.ongil.backend.domain.advertisement.dto.response.UserAdPreferenceResponse;
import com.ongil.backend.domain.advertisement.service.AdvertisementService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Advertisement", description = "광고 관련 API")
@Validated
@RestController
@RequestMapping("/api/advertisements")
@RequiredArgsConstructor
public class AdvertisementController {

	private final AdvertisementService advertisementService;

	@Operation(summary = "광고 생성", description = "새로운 광고를 생성합니다. (관리자)")
	@PostMapping
	public DataResponse<AdvertisementResponse> createAdvertisement(
		@Valid @RequestBody AdvertisementCreateRequest request
	) {
		AdvertisementResponse response = advertisementService.createAdvertisement(request);
		return DataResponse.created(response);
	}

	@Operation(summary = "광고 수정", description = "기존 광고를 수정합니다. (관리자)")
	@PutMapping("/{advertisementId}")
	public DataResponse<AdvertisementResponse> updateAdvertisement(
		@PathVariable Long advertisementId,
		@Valid @RequestBody AdvertisementUpdateRequest request
	) {
		AdvertisementResponse response = advertisementService.updateAdvertisement(advertisementId, request);
		return DataResponse.from(response);
	}

	@Operation(summary = "광고 삭제", description = "광고를 삭제합니다. (관리자)")
	@DeleteMapping("/{advertisementId}")
	public DataResponse<Void> deleteAdvertisement(@PathVariable Long advertisementId) {
		advertisementService.deleteAdvertisement(advertisementId);
		return DataResponse.ok();
	}

	@Operation(summary = "광고 상세 조회", description = "광고의 상세 정보를 조회합니다.")
	@GetMapping("/{advertisementId}")
	public DataResponse<AdvertisementResponse> getAdvertisement(@PathVariable Long advertisementId) {
		AdvertisementResponse response = advertisementService.getAdvertisement(advertisementId);
		return DataResponse.from(response);
	}

	@Operation(summary = "전체 광고 목록 조회", description = "모든 광고 목록을 조회합니다. (관리자)")
	@GetMapping
	public DataResponse<List<AdvertisementResponse>> getAllAdvertisements() {
		List<AdvertisementResponse> responses = advertisementService.getAllAdvertisements();
		return DataResponse.from(responses);
	}

	@Operation(summary = "활성 광고 목록 조회", description = "현재 활성화된 광고 목록을 조회합니다.")
	@GetMapping("/active")
	public DataResponse<List<AdvertisementResponse>> getActiveAdvertisements() {
		List<AdvertisementResponse> responses = advertisementService.getActiveAdvertisements();
		return DataResponse.from(responses);
	}

	@Operation(summary = "사용자 맞춤 광고 조회", description = "사용자의 선호도에 맞춘 광고 목록을 조회합니다.")
	@GetMapping("/personalized")
	public DataResponse<List<AdvertisementResponse>> getPersonalizedAdvertisements(
		@RequestParam Long userId
	) {
		List<AdvertisementResponse> responses = advertisementService.getPersonalizedAdvertisements(userId);
		return DataResponse.from(responses);
	}

	@Operation(summary = "광고 노출 수 증가", description = "광고의 노출 수를 증가시킵니다.")
	@PostMapping("/{advertisementId}/impression")
	public DataResponse<Void> incrementImpression(@PathVariable Long advertisementId) {
		advertisementService.incrementImpression(advertisementId);
		return DataResponse.ok();
	}

	@Operation(summary = "광고 클릭 수 증가", description = "광고의 클릭 수를 증가시킵니다.")
	@PostMapping("/{advertisementId}/click")
	public DataResponse<Void> incrementClick(@PathVariable Long advertisementId) {
		advertisementService.incrementClick(advertisementId);
		return DataResponse.ok();
	}

	// User Preference Endpoints
	@Operation(summary = "사용자 광고 선호도 생성", description = "사용자의 광고 선호도를 생성합니다.")
	@PostMapping("/preferences")
	public DataResponse<UserAdPreferenceResponse> createUserPreference(
		@RequestParam Long userId,
		@Valid @RequestBody UserAdPreferenceRequest request
	) {
		UserAdPreferenceResponse response = advertisementService.createUserPreference(userId, request);
		return DataResponse.created(response);
	}

	@Operation(summary = "사용자 광고 선호도 수정", description = "사용자의 광고 선호도를 수정합니다.")
	@PutMapping("/preferences/{preferenceId}")
	public DataResponse<UserAdPreferenceResponse> updateUserPreference(
		@PathVariable Long preferenceId,
		@Valid @RequestBody UserAdPreferenceRequest request
	) {
		UserAdPreferenceResponse response = advertisementService.updateUserPreference(preferenceId, request);
		return DataResponse.from(response);
	}

	@Operation(summary = "사용자 광고 선호도 삭제", description = "사용자의 광고 선호도를 삭제합니다.")
	@DeleteMapping("/preferences/{preferenceId}")
	public DataResponse<Void> deleteUserPreference(@PathVariable Long preferenceId) {
		advertisementService.deleteUserPreference(preferenceId);
		return DataResponse.ok();
	}

	@Operation(summary = "사용자 광고 선호도 목록 조회", description = "사용자의 광고 선호도 목록을 조회합니다.")
	@GetMapping("/preferences")
	public DataResponse<List<UserAdPreferenceResponse>> getUserPreferences(
		@RequestParam Long userId
	) {
		List<UserAdPreferenceResponse> responses = advertisementService.getUserPreferences(userId);
		return DataResponse.from(responses);
	}
}
