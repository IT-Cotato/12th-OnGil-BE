package com.ongil.backend.domain.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.cart.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

	// 중복 장바구니 확인 (같은 상품 + 같은 옵션)
	Optional<Cart> findByUserIdAndProductIdAndSelectedSizeAndSelectedColor(
		Long userId,
		Long productId,
		String selectedSize,
		String selectedColor
	);

	// 사용자별 전체 장바구니 조회
	@EntityGraph(attributePaths = {"product", "product.brand"})
	List<Cart> findByUserIdOrderByCreatedAtDesc(Long userId);
}