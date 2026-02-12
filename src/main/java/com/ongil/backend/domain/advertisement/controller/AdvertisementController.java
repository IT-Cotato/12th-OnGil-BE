package com.ongil.backend.domain.advertisement.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.advertisement.dto.AdvertisementResponse;
import com.ongil.backend.domain.advertisement.service.AdvertisementService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/advertisements")
@Tag(name = "Advertisement", description = "광고/배너 관련 API")
public class AdvertisementController {

	private final AdvertisementService advertisementService;

	@Operation(summary = "홈 화면 할인 광고 목록 조회", description = "메인 배너에 노출될 5개의 광고 목록을 반환합니다.")
	@GetMapping("/home")
	public DataResponse<List<AdvertisementResponse>> getHomeAdvertisements() {
		List<AdvertisementResponse> result = advertisementService.getHomeAdvertisements();
		return DataResponse.from(result);
	}
}