package com.ongil.backend.domain.navigation.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.navigation.converter.NavigationConverter;
import com.ongil.backend.domain.navigation.dto.response.NavigationResponse;
import com.ongil.backend.domain.navigation.repository.NavigationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NavigationService {

	private final NavigationRepository navigationRepository;
	private final NavigationConverter navigationConverter;

	/**
	 * 활성화된 하단 네비게이션 항목 조회
	 * 표시 순서대로 정렬되어 반환
	 */
	public List<NavigationResponse> getActiveNavigationItems() {
		return navigationConverter.toResponseList(
			navigationRepository.findByEnabledTrueOrderByDisplayOrderAsc()
		);
	}

	/**
	 * 모든 하단 네비게이션 항목 조회 (관리자용)
	 * 표시 순서대로 정렬되어 반환
	 */
	public List<NavigationResponse> getAllNavigationItems() {
		return navigationConverter.toResponseList(
			navigationRepository.findAllByOrderByDisplayOrderAsc()
		);
	}
}
