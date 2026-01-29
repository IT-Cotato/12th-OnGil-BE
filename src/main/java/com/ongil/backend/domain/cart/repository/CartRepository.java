package com.ongil.backend.domain.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.ongil.backend.domain.cart.entity.Cart;

import feign.Param;

public interface CartRepository extends JpaRepository<Cart, Long> {

	// 중복 장바구니 확인 (같은 상품 + 같은 옵션)
	Optional<Cart> findByUserIdAndProductIdAndSelectedSizeAndSelectedColor(
		Long userId,
		Long productId,
		String selectedSize,
		String selectedColor
	);

	// 특정 유저의 장바구니에 담긴 상품 종류 수(행 개수) 조회
	long countByUserId(Long userId);

	// 사용자별 전체 장바구니 조회
	@EntityGraph(attributePaths = {"product", "product.brand"})
	List<Cart> findByUserIdOrderByCreatedAtDesc(Long userId);

	// Cart 단건 조회 (소유자 검증 포함)
	@EntityGraph(attributePaths = {"product", "product.brand"})
	@Query("SELECT c FROM Cart c WHERE c.id = :cartId AND c.user.id = :userId")
	Optional<Cart> findByIdAndUserId(
		@Param("cartId") Long cartId,
		@Param("userId") Long userId
	);

	// 개별 삭제 (소유자 검증 포함)
	@Modifying
	@Query("DELETE FROM Cart c WHERE c.id = :cartId AND c.user.id = :userId")
	int deleteByIdAndUserId(
		@Param("cartId") Long cartId,
		@Param("userId") Long userId
	);

	// 선택 삭제 (소유자 검증 포함)
	@Modifying
	@Query("DELETE FROM Cart c WHERE c.id IN :cartIds AND c.user.id = :userId")
	int deleteByIdInAndUserId(
		@Param("cartIds") List<Long> cartIds,
		@Param("userId") Long userId
	);
}