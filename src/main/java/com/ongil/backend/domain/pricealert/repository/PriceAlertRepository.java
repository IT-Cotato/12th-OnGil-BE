package com.ongil.backend.domain.pricealert.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ongil.backend.domain.pricealert.entity.PriceAlert;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {

	// 스케줄러용: 활성 중이고 아직 알림 미발송인 건 전체 조회 (N+1 방지를 위한 fetch join)
	@Query("SELECT pa FROM PriceAlert pa " +
		"JOIN FETCH pa.user " +
		"JOIN FETCH pa.product " +
		"WHERE pa.isActive = true AND pa.isNotified = false")
	List<PriceAlert> findActiveAlertsWithUserAndProduct();

	// 현재 활성 중인 알림 조회 (상품 상세 화면 진입 시)
	Optional<PriceAlert> findByUserIdAndProductIdAndIsActiveTrue(Long userId, Long productId);
}