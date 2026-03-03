package com.ongil.backend.domain.category.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	private final CategoryConverter categoryConverter;
	private final RedisCacheService redisCacheService;

	// 모든 카테고리 조회 (상위 + 하위, 상품이 있는 카테고리만)
	public List<CategoryResponse> getAllCategories() {
		// Redis 캐시 확인
		List<CategoryResponse> cached = redisCacheService.getList(
			CacheKeyConstants.CATEGORIES_ALL,
			CategoryResponse.class
		);

		if (cached != null && !cached.isEmpty()) {
			return cached;
		}

		// Cache Miss → DB 조회
		List<Category> parentCategories = categoryRepository.findAllParentCategoriesWithSub();
		Set<Long> activeCategoryIds = new HashSet<>(productRepository.findCategoryIdsWithOnSaleProducts());

		List<CategoryResponse> response = parentCategories.stream()
			.map(parent -> {
				// 판매 중인 상품이 있는 하위 카테고리만 필터링
				List<SubCategoryResponse> filteredSubs = parent.getSubCategories().stream()
					.filter(sub -> activeCategoryIds.contains(sub.getId()))
					.map(categoryConverter::toSubCategoryResponse)
					.collect(Collectors.toList());

				if (filteredSubs.isEmpty()) {
					return null; // 하위 카테고리에 상품이 전부 없으면 상위 카테고리도 제외
				}

				return categoryConverter.toResponse(parent, filteredSubs);
			})
			.filter(r -> r != null)
			.collect(Collectors.toList());

		if (response.isEmpty()) {
			log.warn("조회된 카테고리가 없습니다.");
			return response;
		}

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

	// 랜덤 카테고리 조회 (상품이 있는 카테고리만)
	public List<CategoryRandomResponse> getRandomCategories(int count) {
		List<Category> allCategories = categoryRepository.findAllByOrderByDisplayOrder();
		Set<Long> activeCategoryIds = new HashSet<>(productRepository.findCategoryIdsWithOnSaleProducts());

		// 상품이 있는 카테고리만 사전 필터링 (상위 카테고리는 하위 중 하나라도 있으면 포함)
		List<Category> activeCategories = allCategories.stream()
			.filter(category -> {
				if (category.getParentCategory() != null) {
					// 하위 카테고리: 직접 확인
					return activeCategoryIds.contains(category.getId());
				}
				// 상위 카테고리: 하위 카테고리 중 하나라도 상품이 있으면 포함
				return category.getSubCategories().stream()
					.anyMatch(sub -> activeCategoryIds.contains(sub.getId()));
			})
			.collect(Collectors.toList());

		Collections.shuffle(activeCategories);

		// 사전 필터링된 카테고리만 썸네일 조회 (DB 호출 최소화)
		List<CategoryRandomResponse> result = new ArrayList<>();
		for (Category category : activeCategories) {
			if (result.size() >= count) break;
			String thumbnailUrl = getTopProductThumbnail(category);
			if (thumbnailUrl != null) {
				result.add(categoryConverter.toRandomResponse(category, thumbnailUrl));
			}
		}
		return result;
	}

	// 추천 하위 카테고리 조회 (상품이 있는 하위 카테고리만)
	public List<CategorySimpleResponse> getRecommendedSubCategories(int count) {
		List<Category> subCategories = categoryRepository.findAllSubCategories();
		Set<Long> activeCategoryIds = new HashSet<>(productRepository.findCategoryIdsWithOnSaleProducts());

		return subCategories.stream()
			.filter(category -> activeCategoryIds.contains(category.getId()))
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