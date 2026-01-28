package com.ongil.backend.domain.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ongil.backend.domain.review.entity.Review;
import com.ongil.backend.domain.review.enums.ReviewStatus;
import com.ongil.backend.domain.review.enums.ReviewType;

import jakarta.persistence.LockModeType;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	// 상품별 리뷰 목록 조회 (필터 없음)
	@EntityGraph(attributePaths = {"user", "orderItem", "product"})
	@Query("SELECT r FROM Review r " +
		"WHERE r.product.id = :productId " +
		"AND r.reviewStatus = :status " +
		"AND r.reviewType = :reviewType")
	Page<Review> findByProductIdAndStatusAndType(
		@Param("productId") Long productId,
		@Param("status") ReviewStatus status,
		@Param("reviewType") ReviewType reviewType,
		Pageable pageable
	);

	// 사이즈/색상 동적 필터 (하나만 있어도 됨)
	@EntityGraph(attributePaths = {"user", "orderItem", "product"})
	@Query("SELECT r FROM Review r " +
		"WHERE r.product.id = :productId " +
		"AND r.reviewStatus = :status " +
		"AND r.reviewType = :reviewType " +
		"AND (:selectedSize IS NULL OR r.orderItem.selectedSize = :selectedSize) " +
		"AND (:selectedColor IS NULL OR r.orderItem.selectedColor = :selectedColor)")
	Page<Review> findByProductIdWithFilters(
		@Param("productId") Long productId,
		@Param("status") ReviewStatus status,
		@Param("reviewType") ReviewType reviewType,
		@Param("selectedSize") String selectedSize,
		@Param("selectedColor") String selectedColor,
		Pageable pageable
	);

	// 유사 체형만 (필터 없음)
	@EntityGraph(attributePaths = {"user", "orderItem", "product"})
	@Query("SELECT r FROM Review r " +
		"WHERE r.product.id = :productId " +
		"AND r.reviewStatus = :status " +
		"AND r.reviewType = :reviewType " +
		"AND r.user.height BETWEEN :minHeight AND :maxHeight " +
		"AND r.user.weight BETWEEN :minWeight AND :maxWeight")
	Page<Review> findByProductIdAndSimilarBodyType(
		@Param("productId") Long productId,
		@Param("status") ReviewStatus status,
		@Param("reviewType") ReviewType reviewType,
		@Param("minHeight") Integer minHeight,
		@Param("maxHeight") Integer maxHeight,
		@Param("minWeight") Integer minWeight,
		@Param("maxWeight") Integer maxWeight,
		Pageable pageable
	);

	// 유사 체형 + 사이즈/색상 동적 필터 (하나만 있어도 됨)
	@EntityGraph(attributePaths = {"user", "orderItem", "product"})
	@Query("SELECT r FROM Review r " +
		"WHERE r.product.id = :productId " +
		"AND r.reviewStatus = :status " +
		"AND r.reviewType = :reviewType " +
		"AND r.user.height BETWEEN :minHeight AND :maxHeight " +
		"AND r.user.weight BETWEEN :minWeight AND :maxWeight " +
		"AND (:selectedSize IS NULL OR r.orderItem.selectedSize = :selectedSize) " +
		"AND (:selectedColor IS NULL OR r.orderItem.selectedColor = :selectedColor)")
	Page<Review> findByProductIdWithFiltersAndSimilarBodyType(
		@Param("productId") Long productId,
		@Param("status") ReviewStatus status,
		@Param("reviewType") ReviewType reviewType,
		@Param("minHeight") Integer minHeight,
		@Param("maxHeight") Integer maxHeight,
		@Param("minWeight") Integer minWeight,
		@Param("maxWeight") Integer maxWeight,
		@Param("selectedSize") String selectedSize,
		@Param("selectedColor") String selectedColor,
		Pageable pageable
	);

	// 상품별 사이즈 통계(유사 체형 X)
	@Query("SELECT r.sizeAnswer, COUNT(r) FROM Review r " +
		"WHERE r.product.id = :productId " +
		"AND r.reviewStatus = 'COMPLETED' " +
		"AND r.reviewType = :reviewType " +
		"AND r.sizeAnswer IS NOT NULL " +
		"GROUP BY r.sizeAnswer")
	List<Object[]> countBySizeAnswer(
		@Param("productId") Long productId,
		@Param("reviewType") ReviewType reviewType
	);

	// 유사 체형 사용자 사이즈 통계
	@Query("SELECT r.sizeAnswer, COUNT(r) FROM Review r " +
		"WHERE r.product.id = :productId " +
		"AND r.reviewStatus = 'COMPLETED' " +
		"AND r.reviewType = :reviewType " +
		"AND r.sizeAnswer IS NOT NULL " +
		"AND r.user.height BETWEEN :minHeight AND :maxHeight " +
		"AND r.user.weight BETWEEN :minWeight AND :maxWeight " +
		"GROUP BY r.sizeAnswer")
	List<Object[]> countBySizeAnswerWithSimilarBodyType(
		@Param("productId") Long productId,
		@Param("reviewType") ReviewType reviewType,
		@Param("minHeight") Integer minHeight,
		@Param("maxHeight") Integer maxHeight,
		@Param("minWeight") Integer minWeight,
		@Param("maxWeight") Integer maxWeight
	);

	// 색상 통계
	@Query("SELECT r.colorAnswer, COUNT(r) FROM Review r " +
		"WHERE r.product.id = :productId " +
		"AND r.reviewStatus = 'COMPLETED' " +
		"AND r.reviewType = :reviewType " +
		"AND r.colorAnswer IS NOT NULL " +
		"GROUP BY r.colorAnswer")
	List<Object[]> countByColorAnswer(
		@Param("productId") Long productId,
		@Param("reviewType") ReviewType reviewType
	);

	// 소재 통계
	@Query("SELECT r.materialAnswer, COUNT(r) FROM Review r " +
		"WHERE r.product.id = :productId " +
		"AND r.reviewStatus = 'COMPLETED' " +
		"AND r.reviewType = :reviewType " +
		"AND r.materialAnswer IS NOT NULL " +
		"GROUP BY r.materialAnswer")
	List<Object[]> countByMaterialAnswer(
		@Param("productId") Long productId,
		@Param("reviewType") ReviewType reviewType
	);

	// 상품별 평균 별점
	@Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r " +
		"WHERE r.product.id = :productId " +
		"AND r.reviewStatus = 'COMPLETED'")
	Double getAverageRating(@Param("productId") Long productId);

	// 상품별 리뷰 개수(초기 or 한달 후)
	@Query("SELECT COUNT(r) FROM Review r " +
		"WHERE r.product.id = :productId " +
		"AND r.reviewStatus = 'COMPLETED' " +
		"AND r.reviewType = :reviewType")
	Long countByProductIdAndType(
		@Param("productId") Long productId,
		@Param("reviewType") ReviewType reviewType
	);

	// 내가 작성한 리뷰 조회
	@EntityGraph(attributePaths = {"product", "product.brand", "orderItem"})
	Page<Review> findByUserIdAndReviewStatus(Long userId, ReviewStatus status, Pageable pageable);

	// 내가 작성한 특정 타입 리뷰 조회
	@EntityGraph(attributePaths = {"product", "product.brand", "orderItem"})
	Page<Review> findByUserIdAndReviewStatusAndReviewType(
		Long userId,
		ReviewStatus status,
		ReviewType reviewType,
		Pageable pageable
	);

	// OrderItem에 대해 이미 작성된 리뷰 타입 확인
	boolean existsByOrderItemIdAndReviewType(Long orderItemId, ReviewType reviewType);

	// N+1 문제 해결: 여러 OrderItem에 대해 리뷰 작성된 ID 목록 한번에 조회
	@Query("SELECT r.orderItem.id FROM Review r " +
		"WHERE r.orderItem.id IN :orderItemIds " +
		"AND r.reviewType = :reviewType")
	List<Long> findReviewedOrderItemIds(
		@Param("orderItemIds") List<Long> orderItemIds,
		@Param("reviewType") ReviewType reviewType
	);

	// ReviewRepository.java

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT r FROM Review r WHERE r.id = :reviewId")
	Optional<Review> findByIdWithLock(@Param("reviewId") Long reviewId);
}
