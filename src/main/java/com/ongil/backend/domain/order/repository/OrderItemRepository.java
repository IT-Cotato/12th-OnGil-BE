package com.ongil.backend.domain.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ongil.backend.domain.order.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	// 사용자의 주문 상품 목록 조회 (리뷰 작성 가능 목록용)
	@EntityGraph(attributePaths = {"product", "product.brand", "order"})
	@Query("SELECT oi FROM OrderItem oi " +
		"WHERE oi.order.user.id = :userId " +
		"ORDER BY oi.order.createdAt DESC")
	List<OrderItem> findByOrderUserIdWithProduct(@Param("userId") Long userId);

	// 사용자가 구매한 상품 ID 목록 (추천 제외용)
	@Query("SELECT DISTINCT oi.product.id FROM OrderItem oi WHERE oi.order.user.id = :userId")
	List<Long> findProductIdsByUserId(@Param("userId") Long userId);
}
