package com.ongil.backend.domain.wishlist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ongil.backend.domain.wishlist.entity.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

	// 중복 찜 확인
	boolean existsByUserIdAndProductId(Long userId, Long productId);

	// 사용자별 전체 찜 목록 조회
	@EntityGraph(attributePaths = {"product", "product.brand", "product.category"})
	List<Wishlist> findByUserIdOrderByCreatedAtDesc(Long userId);

	// 사용자 + 카테고리별 찜 목록 조회
	@EntityGraph(attributePaths = {"product", "product.brand", "product.category"})
	@Query("SELECT w FROM Wishlist w " +
		"JOIN w.product.category c " +
		"WHERE w.user.id = :userId " +
		"AND (c.id = :categoryId OR c.parentCategory.id = :categoryId) " +
		"ORDER BY w.createdAt DESC")
	List<Wishlist> findByUserIdAndCategoryWithProduct(
		@Param("userId") Long userId,
		@Param("categoryId") Long categoryId
	);
}