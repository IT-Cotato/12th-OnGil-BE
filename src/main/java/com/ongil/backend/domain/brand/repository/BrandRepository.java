package com.ongil.backend.domain.brand.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ongil.backend.domain.brand.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {

	// 브랜드 이름 오름차순 정렬 조회
	@Query("SELECT b FROM Brand b ORDER BY b.name ASC")
	List<Brand> findAllOrderByName();

	@Query(value = "SELECT b.* FROM brands b " +
		"WHERE EXISTS (SELECT 1 FROM products p WHERE p.brand_id = b.brand_id AND p.on_sale = true AND p.product_type <> 'SPECIAL_SALE') " +
		"ORDER BY RAND() LIMIT 3", nativeQuery = true)
	List<Brand> findRandomBrands();
}