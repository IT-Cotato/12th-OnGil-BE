package com.ongil.backend.domain.navigation.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.navigation.dto.response.NavigationResponse;
import com.ongil.backend.domain.navigation.service.NavigationService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Navigation", description = "하단 네비게이션 바 API")
@RestController
@RequestMapping("/api/navigation")
@RequiredArgsConstructor
public class NavigationController {

	private final NavigationService navigationService;

	@Operation(summary = "하단 네비게이션 항목 조회", description = "활성화된 하단 네비게이션 바 항목을 표시 순서대로 조회합니다.")
	@GetMapping
	public DataResponse<List<NavigationResponse>> getNavigationItems() {
		List<NavigationResponse> navigationItems = navigationService.getActiveNavigationItems();
		return DataResponse.from(navigationItems);
	}

	@Operation(summary = "전체 네비게이션 항목 조회", description = "모든 하단 네비게이션 항목을 표시 순서대로 조회합니다. (관리자용)")
	@GetMapping("/all")
	public DataResponse<List<NavigationResponse>> getAllNavigationItems() {
		List<NavigationResponse> navigationItems = navigationService.getAllNavigationItems();
		return DataResponse.from(navigationItems);
	}
}
