package com.ongil.backend.domain.category.service;

import java.util.ArrayList;
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
import com.ongil.backend.global.config.redis.CacheKeyConstants;
import com.ongil.backend.global.config.redis.RedisCacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	private final CategoryConverter categoryConverter;
	private final RedisCacheService redisCacheService;

	public List<CategoryResponse> getAllCategories() {
		// Redis 캐시 확인
		List<CategoryResponse> cached = redisCacheService.get(
			CacheKeyConstants.CATEGORIES_ALL,
			List.class
		);

		if (cached != null) {
			return cached;
		}

		// Cache Miss → DB 조회
		List<Category> parentCategories = categoryRepository.findAllParentCategoriesWithSub();
		List<CategoryResponse> response = categoryConverter.toResponseList(parentCategories);

		// Redis 캐싱 (무한 TTL)
		redisCacheService.save(
			CacheKeyConstants.CATEGORIES_ALL,
			response,
			CacheKeyConstants.MASTER_DATA_TTL_HOURS
		);

		return response;
	}

	public List<SubCategoryResponse> getSubCategories(Long parentCategoryId) {
		List<Category> subCategories = categoryRepository.findSubCategoriesByParentId(parentCategoryId);
		return categoryConverter.toSubCategoryResponseList(subCategories);
	}

	public List<CategoryRandomResponse> getRandomCategories(int count) {
		List<Category> allCategories = categoryRepository.findAllByOrderByDisplayOrder();

		List<Category> shuffledCategories = new ArrayList<>(allCategories);
		Collections.shuffle(shuffledCategories);

		return shuffledCategories.stream()
			.limit(count)
			.map(category -> {
				String thumbnailUrl = getTopProductThumbnail(category);
				return categoryConverter.toRandomResponse(category, thumbnailUrl);
			})
			.collect(Collectors.toList());
	}

	public List<CategorySimpleResponse> getRecommendedSubCategories(int count) {
		List<Category> subCategories = categoryRepository.findAllSubCategories();

		return subCategories.stream()
			.limit(count)
			.map(categoryConverter::toSimpleResponse)
			.collect(Collectors.toList());
	}

	private String getTopProductThumbnail(Category category) {
		Long targetCategoryId;

		if (category.getParentCategory() == null) {
			if (category.getSubCategories().isEmpty()) {
				return null;
			}
			targetCategoryId = category.getSubCategories().get(0).getId();
		} else {
			targetCategoryId = category.getId();
		}

		Product topProduct = productRepository
			.findTopByCategoryIdOrderByPopularity(targetCategoryId)
			.orElse(null);

		return topProduct != null ? getFirstImage(topProduct.getImageUrls()) : null;
	}

	private String getFirstImage(String imageUrls) {
		if (imageUrls == null || imageUrls.trim().isEmpty()) {
			return null;
		}
		return imageUrls.split(",")[0].trim();
	}
}