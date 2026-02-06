package com.ongil.backend.domain.order.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	// 주문 내역 조회 (기간 + 키워드 검색)
	@Query("SELECT DISTINCT o FROM Order o " +
		"LEFT JOIN FETCH o.orderItems oi " +
		"LEFT JOIN FETCH oi.product p " +
		"WHERE o.user.id = :userId " +
		"AND o.createdAt >= :startDate " +
		"AND o.createdAt <= :endDate " +
		"AND (:keyword IS NULL OR :keyword = '' " +
		"OR o.orderNumber LIKE CONCAT('%', :keyword, '%') " +
		"OR p.name LIKE CONCAT('%', :keyword, '%'))")
	Page<Order> findOrderHistory(
		@Param("userId") Long userId,
		@Param("keyword") String keyword,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate,
		Pageable pageable
	);

	// 주문 내역 개수 조회 (countQuery 분리)
	@Query(value = "SELECT DISTINCT o FROM Order o " +
		"LEFT JOIN o.orderItems oi " +
		"LEFT JOIN oi.product p " +
		"WHERE o.user.id = :userId " +
		"AND o.createdAt >= :startDate " +
		"AND o.createdAt <= :endDate " +
		"AND (:keyword IS NULL OR :keyword = '' " +
		"OR o.orderNumber LIKE CONCAT('%', :keyword, '%') " +
		"OR p.name LIKE CONCAT('%', :keyword, '%'))",
		countQuery = "SELECT COUNT(DISTINCT o) FROM Order o " +
			"LEFT JOIN o.orderItems oi " +
			"LEFT JOIN oi.product p " +
			"WHERE o.user.id = :userId " +
			"AND o.createdAt >= :startDate " +
			"AND o.createdAt <= :endDate " +
			"AND (:keyword IS NULL OR :keyword = '' " +
			"OR o.orderNumber LIKE CONCAT('%', :keyword, '%') " +
			"OR p.name LIKE CONCAT('%', :keyword, '%'))")
	Page<Order> findOrderHistoryWithCount(
		@Param("userId") Long userId,
		@Param("keyword") String keyword,
		@Param("startDate") LocalDateTime startDate,
		@Param("endDate") LocalDateTime endDate,
		Pageable pageable
	);
}