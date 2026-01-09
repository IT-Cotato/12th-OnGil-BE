package com.ongil.backend.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ongil.backend.domain.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	//조회수 증가 (동시성 안전)
	@Modifying
	@Query("UPDATE Product p SET p.viewCount = p.viewCount + 1 WHERE p.id = :productId")
	void incrementViewCount(@Param("productId") Long productId);
}