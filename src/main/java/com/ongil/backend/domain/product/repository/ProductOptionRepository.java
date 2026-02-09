package com.ongil.backend.domain.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.entity.ProductOption;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

	// 상품별 모든 옵션 조회
	List<ProductOption> findByProductId(Long productId);

	// 특정 옵션 조회 (사이즈, 색상)
	Optional<ProductOption> findByProductIdAndSizeAndColor(Long productId, String size, String color);

	// 재고 부족 옵션 조회 (5개 이하)
	List<ProductOption> findByProductIdAndStockBetween(Long productId, int min, int max);

	// 품절 옵션 조회 (재고 0)
	List<ProductOption> findByProductIdAndStock(Long productId, int stock);

	// 재고 있는 옵션 조회 (재고 1개 이상)
	List<ProductOption> findByProductIdAndStockGreaterThan(Long productId, int stock);

	// 특정 상품의 모든 옵션 삭제
	void deleteByProduct(Product product);
}
