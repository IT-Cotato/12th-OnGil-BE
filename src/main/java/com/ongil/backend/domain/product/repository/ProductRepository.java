package com.ongil.backend.domain.product.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.enums.ProductType;

public interface ProductRepository extends JpaRepository<Product, Long> {

	// 조회수 증가
	@Modifying
	@Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.id = :productId")
	void incrementViewCount(@Param("productId") Long productId);

	// 조건에 따른 상품 조회
	@EntityGraph(attributePaths = {"brand", "category"})
	@Query("""
		SELECT p FROM Product p
		WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
		  AND (:brandId IS NULL OR p.brand.id = :brandId)
		  AND (:minPrice IS NULL OR p.price >= :minPrice)
		  AND (:maxPrice IS NULL OR p.price <= :maxPrice)
		  AND (:size IS NULL OR p.sizes LIKE CONCAT('%', :size, '%'))
		  AND p.onSale = true
		""")
	Page<Product> findAllByCondition(
		@Param("categoryId") Long categoryId,
		@Param("brandId") Long brandId,
		@Param("minPrice") Integer minPrice,
		@Param("maxPrice") Integer maxPrice,
		@Param("size") String size,
		Pageable pageable
	);

	// 특가 상품 조회
	@EntityGraph(attributePaths = {"brand", "category"})
	Page<Product> findByOnSaleTrueAndProductTypeOrderByDiscountRateDesc(
		ProductType productType,
		Pageable pageable
	);

	// 비슷한 상품 조회(같은 카테고리 + 비슷한 가격대)
	@EntityGraph(attributePaths = {"brand", "category"})
	Page<Product> findByOnSaleTrueAndCategoryIdAndIdNotAndPriceBetween(
		Long categoryId,
		Long excludeProductId,
		Integer minPrice,
		Integer maxPrice,
		Pageable pageable
	);

	// 키워드 검색 (브랜드명, 카테고리명, 색상, 상품명)
	@EntityGraph(attributePaths = {"brand", "category"})
	@Query("""
		SELECT p FROM Product p
		WHERE p.onSale = true
		  AND (
		    LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    OR LOWER(p.brand.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    OR LOWER(p.colors) LIKE LOWER(CONCAT('%', :keyword, '%'))
		  )
		""")
	Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

	// 브랜드별 상품 조회 (페이징)
	@EntityGraph(attributePaths = {"brand", "category"})
	Page<Product> findByBrandId(Long brandId, Pageable pageable);

	@Query("SELECT p FROM Product p " +
		"WHERE p.category.id = :categoryId " +
		"AND p.onSale = true " +  // ✅ 판매 중인 상품만!
		"ORDER BY (p.viewCount + p.purchaseCount) DESC " +
		"LIMIT 1")
	Optional<Product> findTopByCategoryIdOrderByPopularity(@Param("categoryId") Long categoryId);
}