package com.ongil.backend.domain.category.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.category.converter.CategoryConverter;
import com.ongil.backend.domain.category.dto.response.CategoryRandomResponse;
import com.ongil.backend.domain.category.dto.response.CategoryResponse;
import com.ongil.backend.domain.category.dto.response.CategorySimpleResponse;
import com.ongil.backend.domain.category.dto.response.SubCategoryResponse;
import com.ongil.backend.domain.category.entity.Category;
import com.ongil.backend.domain.category.repository.CategoryRepository;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	private final CategoryConverter categoryConverter;

	// 모든 카테고리 조회 (상위 + 하위)
	public List<CategoryResponse> getAllCategories() {
		List<Category> parentCategories = categoryRepository.findAllParentCategoriesWithSub();
		return categoryConverter.toResponseList(parentCategories);
	}

	// 특정 상위 카테고리의 하위 카테고리 조회
	public List<SubCategoryResponse> getSubCategories(Long parentCategoryId) {
		List<Category> subCategories = categoryRepository.findSubCategoriesByParentId(parentCategoryId);
		return categoryConverter.toSubCategoryResponseList(subCategories);
	}

	// 랜덤 카테고리 조회 (홈 화면용)
	public List<CategoryRandomResponse> getRandomCategories(int count) {
		List<Category> allCategories = categoryRepository.findAllByOrderByDisplayOrder();
		Collections.shuffle(allCategories); // 랜덤 섞기

		return allCategories.stream()
			.limit(count)
			.map(category -> {
				String thumbnailUrl = getTopProductThumbnail(category);
				return categoryConverter.toRandomResponse(category, thumbnailUrl);
			})
			.collect(Collectors.toList());
	}

	// 추천 하위 카테고리 조회
	public List<CategorySimpleResponse> getRecommendedSubCategories(int count) {
		List<Category> subCategories = categoryRepository.findAllSubCategories();

		return subCategories.stream()
			.limit(count)
			.map(categoryConverter::toSimpleResponse)
			.collect(Collectors.toList());
	}

	private String getTopProductThumbnail(Category category) {
		Long targetCategoryId;

		// 상위 카테고리인 경우: 첫 번째 하위 카테고리의 인기 1등
		if (category.getParentCategory() == null) {
			if (category.getSubCategories().isEmpty()) {
				return null;
			}
			targetCategoryId = category.getSubCategories().get(0).getId();
		} else {
			// 하위 카테고리인 경우: 해당 카테고리의 인기 1등
			targetCategoryId = category.getId();
		}

		// 인기순 정렬 (조회수 + 구매수)
		Product topProduct = productRepository
			.findTopByCategoryIdOrderByPopularity(targetCategoryId)
			.orElse(null);

		return topProduct != null ? getFirstImage(topProduct.getImageUrls()) : null;
	}

	// 쉼표로 구분된 이미지 URL에서 첫 번째 추출
	private String getFirstImage(String imageUrls) {
		if (imageUrls == null || imageUrls.trim().isEmpty()) {
			return null;
		}
		return imageUrls.split(",")[0].trim();
	}
}
