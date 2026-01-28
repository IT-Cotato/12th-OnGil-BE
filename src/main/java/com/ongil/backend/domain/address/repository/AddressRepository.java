package com.ongil.backend.domain.address.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ongil.backend.domain.address.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

	// 사용자의 모든 배송지 조회 (최신순)
	List<Address> findByUserIdOrderByCreatedAtDesc(Long userId);

	// 사용자의 특정 배송지 조회
	Optional<Address> findByIdAndUserId(Long id, Long userId);

	// 사용자의 기본 배송지 조회
	Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);

	// 사용자의 배송지 존재 여부 확인
	boolean existsByIdAndUserId(Long id, Long userId);

	// 사용자의 기본 배송지를 모두 일반 배송지로 변경
	@Modifying
	@Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId AND a.isDefault = true")
	void unsetDefaultAddress(@Param("userId") Long userId);

	// 배송지 개별 삭제
	int deleteByIdAndUserId(Long id, Long userId);
}