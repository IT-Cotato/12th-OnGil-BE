package com.ongil.backend.domain.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ongil.backend.domain.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	// 모든 상위 카테고리 조회 (하위 카테고리 포함)
	@Query("SELECT c FROM Category c " +
		"LEFT JOIN FETCH c.subCategories " +
		"WHERE c.parentCategory IS NULL " +
		"ORDER BY c.displayOrder")
	List<Category> findAllParentCategoriesWithSub();

	// 모든 카테고리 조회 (상위 + 하위)
	List<Category> findAllByOrderByDisplayOrder();

	// 하위 카테고리만 조회
	@Query("SELECT c FROM Category c " +
		"WHERE c.parentCategory IS NOT NULL " +
		"ORDER BY c.displayOrder")
	List<Category> findAllSubCategories();

	// 특정 상위 카테고리의 하위 카테고리 조회
	@Query("SELECT c FROM Category c " +
		"WHERE c.parentCategory.id = :parentCategoryId " +
		"ORDER BY c.displayOrder")
	List<Category> findSubCategoriesByParentId(@Param("parentCategoryId") Long parentCategoryId);
}