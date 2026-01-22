package com.ongil.backend.domain.category.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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

	// 모든 카테고리 조회 (상위 + 하위)
	public List<CategoryResponse> getAllCategories() {
		// Redis 캐시 확인
		List<CategoryResponse> cached = redisCacheService.getList(
			CacheKeyConstants.CATEGORIES_ALL,
			CategoryResponse.class
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

	// 특정 상위 카테고리의 하위 카테고리 조회
	public List<SubCategoryResponse> getSubCategories(Long parentCategoryId) {
		List<Category> subCategories = categoryRepository.findSubCategoriesByParentId(parentCategoryId);
		return categoryConverter.toSubCategoryResponseList(subCategories);
	}

	// 랜덤 카테고리 조회
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

	// 추천 하위 카테고리 조회
	public List<CategorySimpleResponse> getRecommendedSubCategories(int count) {
		List<Category> subCategories = categoryRepository.findAllSubCategories();

		return subCategories.stream()
			.limit(count)
			.map(categoryConverter::toSimpleResponse)
			.collect(Collectors.toList());
	}

	// 오늘의 추천 카테고리 조회 (매일 다른 카테고리)
	public List<CategorySimpleResponse> getTodayRecommendedCategories(int count) {
		LocalDate today = LocalDate.now();
		// Redis 캐시 키에 날짜 포함
		String cacheKey = CacheKeyConstants.TODAY_RECOMMENDED_CATEGORIES + ":" + today;
		
		// Redis 캐시 확인
		List<CategorySimpleResponse> cached = redisCacheService.getList(
			cacheKey,
			CategorySimpleResponse.class
		);

		if (cached != null && cached.size() >= count) {
			return cached.stream()
				.limit(count)
				.collect(Collectors.toList());
		}

		// Cache Miss → DB 조회 후 날짜 기반 셔플
		List<Category> subCategories = categoryRepository.findAllSubCategories();
		
		// 날짜를 시드로 사용하여 매일 다른 순서로 셔플
		List<Category> shuffledCategories = new ArrayList<>(subCategories);
		long seed = today.toEpochDay(); // 날짜를 시드로 사용
		Collections.shuffle(shuffledCategories, new Random(seed));

		List<CategorySimpleResponse> response = shuffledCategories.stream()
			.limit(count)
			.map(categoryConverter::toSimpleResponse)
			.collect(Collectors.toList());

		// Redis 캐싱 (하루 동안 유지)
		redisCacheService.save(
			cacheKey,
			response,
			CacheKeyConstants.DAILY_TTL_HOURS
		);

		return response;
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