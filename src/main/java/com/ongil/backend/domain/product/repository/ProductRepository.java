package com.ongil.backend.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ongil.backend.domain.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	/**
	 * 조회수 증가 (동시성 안전)
	 */
	@Modifying
	@Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.id = :productId")
	void incrementViewCount(@Param("productId") Long productId);

	/**
	 * 조건에 맞는 상품들을 페이징 조회
	 *
	 * @EntityGraph: FETCH JOIN 대신 사용 (Pageable과 호환)
	 */
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
}