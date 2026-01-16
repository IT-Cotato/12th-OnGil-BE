package com.ongil.backend.domain.navigation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.navigation.entity.Navigation;

public interface NavigationRepository extends JpaRepository<Navigation, Long> {

	/**
	 * 활성화된 네비게이션 항목을 표시 순서대로 조회
	 */
	List<Navigation> findByEnabledTrueOrderByDisplayOrderAsc();

	/**
	 * 모든 네비게이션 항목을 표시 순서대로 조회
	 */
	List<Navigation> findAllByOrderByDisplayOrderAsc();
}
