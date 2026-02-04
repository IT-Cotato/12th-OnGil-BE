package com.ongil.backend.domain.order.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ongil.backend.domain.order.entity.Order;
import com.ongil.backend.domain.order.enums.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

	// 특정 사용자의 주문 확정 시간 범위로 주문 조회
	@Query("SELECT o FROM Order o " +
		"WHERE o.user.id = :userId " +
		"AND o.orderStatus = :status " +
		"AND o.confirmedAt >= :startTime " +
		"AND o.confirmedAt < :endTime " +
		"ORDER BY o.confirmedAt DESC")
	List<Order> findByUserIdAndStatusAndConfirmedAtBetween(
		@Param("userId") Long userId,
		@Param("status") OrderStatus status,
		@Param("startTime") LocalDateTime startTime,
		@Param("endTime") LocalDateTime endTime
	);

	// 특정 사용자의 주문 확정 시간 이후 주문 조회 (5일 전 이후 확정된 주문)
	@Query("SELECT o FROM Order o " +
		"WHERE o.user.id = :userId " +
		"AND o.orderStatus = :status " +
		"AND o.confirmedAt >= :afterTime " +
		"ORDER BY o.confirmedAt ASC")
	List<Order> findByUserIdAndStatusAndConfirmedAtAfter(
		@Param("userId") Long userId,
		@Param("status") OrderStatus status,
		@Param("afterTime") LocalDateTime afterTime
	);

	// 특정 사용자의 특정 상태 주문 조회
	@Query("SELECT o FROM Order o " +
		"WHERE o.user.id = :userId " +
		"AND o.orderStatus = :status " +
		"ORDER BY o.confirmedAt DESC")
	List<Order> findByUserIdAndStatus(
		@Param("userId") Long userId,
		@Param("status") OrderStatus status
	);

	// 특정 사용자의 주문 확정 시간 이전 주문 조회 (한달 이상 경과)
	@Query("SELECT o FROM Order o " +
		"WHERE o.user.id = :userId " +
		"AND o.orderStatus = :status " +
		"AND o.confirmedAt <= :beforeTime " +
		"ORDER BY o.confirmedAt DESC")
	List<Order> findByUserIdAndStatusAndConfirmedAtBefore(
		@Param("userId") Long userId,
		@Param("status") OrderStatus status,
		@Param("beforeTime") LocalDateTime beforeTime
	);

	// 특정 사용자의 전체 주문 조회
	@Query("SELECT o FROM Order o " +
		"WHERE o.user.id = :userId " +
		"ORDER BY o.createdAt DESC")
	List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}