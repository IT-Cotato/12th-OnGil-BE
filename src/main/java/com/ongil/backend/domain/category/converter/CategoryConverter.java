package com.ongil.backend.domain.category.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.category.dto.response.CategoryRandomResponse;
import com.ongil.backend.domain.category.dto.response.CategoryResponse;
import com.ongil.backend.domain.category.dto.response.CategorySimpleResponse;
import com.ongil.backend.domain.category.dto.response.SubCategoryResponse;
import com.ongil.backend.domain.category.entity.Category;
import com.ongil.backend.domain.category.enums.CategoryType;

@Component
public class CategoryConverter {

	/**
	 * Category → CategoryResponse (하위 카테고리 포함)
	 */
	public CategoryResponse toResponse(Category category) {
		return CategoryResponse.builder()
			.categoryId(category.getId())
			.name(category.getName())
			.iconUrl(category.getIconUrl())
			.displayOrder(category.getDisplayOrder())
			.subCategories(category.getSubCategories().stream()
				.map(this::toSubCategoryResponse)
				.collect(Collectors.toList()))
			.build();
	}

	public List<CategoryResponse> toResponseList(List<Category> categories) {
		return categories.stream()
			.map(this::toResponse)
			.collect(Collectors.toList());
	}

	/**
	 * Category → SubCategoryResponse
	 */
	public SubCategoryResponse toSubCategoryResponse(Category category) {
		return SubCategoryResponse.builder()
			.categoryId(category.getId())
			.name(category.getName())
			.iconUrl(category.getIconUrl())
			.displayOrder(category.getDisplayOrder())
			.build();
	}

	public List<SubCategoryResponse> toSubCategoryResponseList(List<Category> categories) {
		return categories.stream()
			.map(this::toSubCategoryResponse)
			.collect(Collectors.toList());
	}

	/**
	 * Category → CategoryRandomResponse (상품 썸네일 포함)
	 * 홈 화면용 - 상품 이미지 사용
	 */
	public CategoryRandomResponse toRandomResponse(Category category, String thumbnailUrl) {
		return CategoryRandomResponse.builder()
			.categoryId(category.getId())
			.name(category.getName())
			.categoryType(
				category.getParentCategory() == null
					? CategoryType.PARENT
					: CategoryType.CHILD
			)
			.thumbnailUrl(thumbnailUrl)  // 상품 이미지 (인기 1등)
			.displayOrder(category.getDisplayOrder())
			.build();
	}

	/**
	 * Category → CategorySimpleResponse (아이콘만)
	 * 카테고리 탭용 - 카테고리 아이콘 사용
	 */
	public CategorySimpleResponse toSimpleResponse(Category category) {
		return CategorySimpleResponse.builder()
			.categoryId(category.getId())
			.name(category.getName())
			.iconUrl(category.getIconUrl())
			.displayOrder(category.getDisplayOrder())
			.build();
	}

	public List<CategorySimpleResponse> toSimpleResponseList(List<Category> categories) {
		return categories.stream()
			.map(this::toSimpleResponse)
			.collect(Collectors.toList());
	}
}