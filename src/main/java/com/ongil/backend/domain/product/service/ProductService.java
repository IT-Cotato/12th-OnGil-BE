package com.ongil.backend.domain.product.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.ongil.backend.domain.product.converter.ProductConverter;
import com.ongil.backend.domain.product.converter.SizeGuideConverter;
import com.ongil.backend.domain.product.dto.request.ProductSearchCondition;
import com.ongil.backend.domain.product.dto.response.AiMaterialDescriptionResponse;
import com.ongil.backend.domain.product.dto.response.ProductDetailResponse;
import com.ongil.backend.domain.product.dto.response.ProductOptionResponse;
import com.ongil.backend.domain.product.dto.response.ProductSearchPageResDto;
import com.ongil.backend.domain.product.dto.response.ProductSimpleResponse;

import com.ongil.backend.domain.product.dto.response.SizeGuideResponse;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.entity.ProductOption;
import com.ongil.backend.domain.product.entity.ProductViewHistory;
import com.ongil.backend.domain.product.enums.ProductSortType;
import com.ongil.backend.domain.product.enums.ProductType;
import com.ongil.backend.domain.product.repository.ProductOptionRepository;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.domain.product.repository.ProductViewHistoryRepository;
import com.ongil.backend.domain.search.service.RecentSearchService;
import com.ongil.backend.domain.search.service.SearchService;
import com.ongil.backend.domain.search.validator.SearchValidator;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.domain.category.entity.Category;
import com.ongil.backend.domain.category.repository.CategoryRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductOptionRepository productOptionRepository;
	private final ProductViewHistoryRepository productViewHistoryRepository;
	private final ProductConverter productConverter;
	private final AiMaterialService aiMaterialService;
	private final UserRepository userRepository;
	private final SizeGuideConverter sizeGuideConverter;
	private final SearchService searchService;
	private final RecentSearchService recentSearchService;
	private final CategoryRepository categoryRepository;

	private static final int SIMILAR_CUSTOMERS_LIMIT = 4;

	// 상품 상세 조회
	@Transactional
	public ProductDetailResponse getProductDetail(Long productId, Long userId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

		productRepository.incrementViewCount(productId);

		// 로그인 사용자인 경우 조회 기록 저장
		if (userId != null) {
			saveProductViewHistory(userId, product);
		}

		if (needsAiDescription(product)) {
			generateAndSaveAiDescription(product);
		}

		List<ProductOption> options = productOptionRepository.findByProductId(productId);

		return productConverter.toDetailResponse(product, options);
	}

	// 상품별 옵션 목록 조회
	public List<ProductOptionResponse> getProductOptions(Long productId) {
		// 상품 존재 여부 확인
		if (!productRepository.existsById(productId)) {
			throw new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
		}

		List<ProductOption> options = productOptionRepository.findByProductId(productId);
		return productConverter.convertOptions(options);
	}

	// 상품 조회 기록 저장
	private void saveProductViewHistory(Long userId, Product product) {
		try {
			ProductViewHistory viewHistory = ProductViewHistory.builder()
				.user(User.builder().id(userId).build())
				.product(product)
				.build();
			productViewHistoryRepository.save(viewHistory);
		} catch (Exception e) {
			log.error("상품 조회 기록 저장 실패: userId={}, productId={}, error={}",
				userId, product.getId(), e.getMessage());
		}
	}

	// 조건에 따른 상품 조회
	public ProductSearchPageResDto getProducts(
		ProductSearchCondition condition,
		ProductSortType sortType,
		Pageable pageable,
		String query,
		Long userId
	) {

		// 검색어(query)가 있을 시 Elasticsearch 관련 동작
		boolean hasQuery = query != null && !query.isBlank();
		List<Long> targetIds = hasQuery ? searchService.getProductIdsByQuery(query) : null;

		if (hasQuery && targetIds.isEmpty()) {
			String keyword = SearchValidator.normalize(query);
			List<String> alternatives = searchService.recommendAlternatives(keyword, 4);
			return ProductSearchPageResDto.of(Page.empty(pageable), alternatives);
		}

		Integer[] priceRange = condition.parsePriceRange();
		Integer minPrice = priceRange != null ? priceRange[0] : null;
		Integer maxPrice = priceRange != null ? priceRange[1] : null;

		Sort sort = createSort(sortType);
		Pageable pageableWithSort = PageRequest.of(
			pageable.getPageNumber(),
			pageable.getPageSize(),
			sort
		);

		Page<Product> products;

		// categoryId가 있으면 상위/하위 카테고리 판단 후 분기 처리
		if (condition.getCategoryId() != null) {
			Category category = categoryRepository.findById(condition.getCategoryId())
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));

			if (category.getParentCategory() == null) {
				// 상위 카테고리 → 하위 카테고리들의 상품 전체 조회
				products = productRepository.findAllByParentCategoryCondition(
					targetIds,
					condition.getCategoryId(),
					condition.getBrandIds(),
					minPrice,
					maxPrice,
					condition.buildSizesPattern(),
					pageableWithSort
				);
			} else {
				// 하위 카테고리 → 해당 카테고리 상품만 조회
				products = productRepository.findAllByCondition(
					targetIds,
					condition.getCategoryId(),
					condition.getBrandIds(),
					minPrice,
					maxPrice,
					condition.buildSizesPattern(),
					pageableWithSort
				);
			}
		} else {
			// categoryId가 없으면 전체 상품 조회
			products = productRepository.findAllByCondition(
				targetIds,
				null,
				condition.getBrandIds(),
				minPrice,
				maxPrice,
				condition.buildSizesPattern(),
				pageableWithSort
			);
		}

		// 추천 검색어에 이용하기 위한 과정
		if (hasQuery && !products.isEmpty()) {
			String representativeKeyword = searchService.extractRepresentativeKeyword(query);
			if (representativeKeyword != null && !representativeKeyword.isBlank()) {
				recordSearchSideEffects(representativeKeyword, userId);
			}
		}

		Page<ProductSimpleResponse> pageRes = products.map(productConverter::toSimpleResponse);
		return ProductSearchPageResDto.of(pageRes, List.of());
	}

	// 로그인 유저) 최근 검색어 저장
	private void recordSearchSideEffects(String query, Long userId) {
		String keyword = SearchValidator.normalize(query);
		if (keyword.isEmpty()) return;

		searchService.saveSearchLog(keyword, userId);
		if (userId != null) {
			try {
				recentSearchService.saveRecentSearch(userId, keyword);
			} catch (Exception e) {
				log.error("Redis 최근 검색어 저장 실패: {}", e.getMessage());
			}
		}
	}

	// 특가 상품 조회
	public List<ProductSimpleResponse> getSpecialSaleProducts() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Product> products = productRepository.findByOnSaleTrueAndProductTypeOrderByDiscountRateDesc(
			ProductType.SPECIAL_SALE,
			pageable
		);
		return productConverter.toSimpleResponseList(products.getContent());
	}

	// 비슷한 상품 조회
	public List<ProductSimpleResponse> getSimilarProducts(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

		int basePrice = product.getPrice();
		int minPrice = (int)(basePrice * 0.8);
		int maxPrice = (int)(basePrice * 1.2);

		Pageable pageable = PageRequest.of(0, 6);
		Page<Product> similarProducts = productRepository.findByOnSaleTrueAndCategoryIdAndIdNotAndPriceBetween(
			product.getCategory().getId(),
			productId,
			minPrice,
			maxPrice,
			pageable
		);

		return similarProducts.map(productConverter::toSimpleResponse).getContent();
	}

	// 키워드 검색
	public Page<ProductSimpleResponse> searchProducts(String keyword, Pageable pageable) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return Page.empty(pageable);
		}

		Page<Product> products = productRepository.searchByKeyword(keyword.trim(), pageable);
		return products.map(productConverter::toSimpleResponse);
	}

	// 사이즈 가이드 기능
	public SizeGuideResponse getSizeGuide(Long productId, Long userId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

		if (!hasBodyInfo(user)) {
			return buildEmptyResponse();
		}

		Integer minHeight = user.getHeight() - 5;
		Integer maxHeight = user.getHeight() + 5;
		Integer minWeight = user.getWeight() - 5;
		Integer maxWeight = user.getWeight() + 5;

		// 유사 고객 구매 통계 조회
		List<Object[]> rawStatistics = productRepository.findSizeStatisticsByProductAndUserBody(
			productId, minHeight, maxHeight, minWeight, maxWeight
		);

		// 유사 고객이 구매한 사이즈와 그 사이즈를 구매한 횟수 조회, 없을시 >> 체형 정보만 반환
		if (rawStatistics.isEmpty()) {
			return buildResponseWithBodyInfoOnly(user, product);
		}

		// 해당 상품을 구매한 유사 고객의 정보(키, 몸무게, 평소 사이즈) (최대 4명)
		Pageable pageable = PageRequest.of(0, SIMILAR_CUSTOMERS_LIMIT);
		List<Object[]> rawCustomers = productRepository.findSimilarCustomersPurchases(
			productId, minHeight, maxHeight, minWeight, maxWeight,
			user.getHeight(), user.getWeight(), pageable
		);

		// 응답 데이터 변환 및 생성
		List<SizeGuideResponse.SizeStatistic> sizeStatistics = sizeGuideConverter.toSizeStatistics(rawStatistics);
		List<SizeGuideResponse.SimilarCustomer> similarCustomers = sizeGuideConverter.toSimilarCustomers(rawCustomers);
		List<String> recommendedSizes = sizeGuideConverter.calculateRecommendedSizes(sizeStatistics);
		SizeGuideResponse.UserBodyInfo userBodyInfo = sizeGuideConverter.toUserBodyInfo(user, product);

		return SizeGuideResponse.builder()
			.recommendedSizes(recommendedSizes)
			.sizeStatistics(sizeStatistics)
			.similarCustomers(similarCustomers)
			.userBodyInfo(userBodyInfo)
			.build();

	}

	// 헬퍼 메서드

	private boolean needsAiDescription(Product product) {
		return product.getAiMaterialAdvantages() == null
			|| product.getAiMaterialDisadvantages() == null
			|| product.getAiMaterialCare() == null;
	}

	private void generateAndSaveAiDescription(Product product) {
		try {
			AiMaterialDescriptionResponse ai = aiMaterialService.generate(product.getMaterialOriginal());

			product.updateAiMaterialDescription(
				ai.getAdvantages(),
				ai.getDisadvantages(),
				ai.getCare()
			);

		} catch (Exception e) {
			AiMaterialDescriptionResponse defaultResponse = AiMaterialDescriptionResponse.createDefault();
			product.updateAiMaterialDescription(
				defaultResponse.getAdvantages(),
				defaultResponse.getDisadvantages(),
				defaultResponse.getCare()
			);
		}
	}

	private Sort createSort(ProductSortType sortType) {
		return switch (sortType) {
			case POPULAR -> Sort.by(
				Sort.Order.desc("popularity"),
				Sort.Order.asc("id")
			);
			case REVIEW -> Sort.by(
				Sort.Order.desc("reviewCount"),   // 1차: 리뷰 많은 순
				Sort.Order.desc("popularity"),    // 2차: 인기순
				Sort.Order.asc("id")
			);
			case PRICE_HIGH -> Sort.by(
				Sort.Order.desc("price"),         // 1차: 가격 높은 순
				Sort.Order.desc("popularity"),     // 2차: 인기순
				Sort.Order.asc("id")              // 3차: id 오름차순
			);
			case PRICE_LOW -> Sort.by(
				Sort.Order.asc("price"),          // 1차: 가격 낮은 순
				Sort.Order.desc("popularity"),     // 2차: 인기순
				Sort.Order.asc("id")
			);
		};
	}

	private boolean hasBodyInfo(User user) {
		return user.getHeight() != null && user.getWeight() != null;
	}

	private SizeGuideResponse buildEmptyResponse() {
		return SizeGuideResponse.builder()
			.recommendedSizes(null)
			.sizeStatistics(null)
			.similarCustomers(null)
			.userBodyInfo(null)
			.build();
	}

	private SizeGuideResponse buildResponseWithBodyInfoOnly(User user, Product product) {
		SizeGuideResponse.UserBodyInfo userBodyInfo = sizeGuideConverter.toUserBodyInfo(user, product);

		return SizeGuideResponse.builder()
			.recommendedSizes(null)
			.sizeStatistics(null)
			.similarCustomers(null)
			.userBodyInfo(userBodyInfo)
			.build();
	}

	// ===== 추천 상품 관련 메서드 =====

	/**
	 * 홈화면 추천 상품 조회
	 * - 인기 상품 순(viewCount + cartCount) 반환
	 * - 특가 상품(SPECIAL_SALE) 제외
	 */
	public List<ProductSimpleResponse> getRecommendedProducts(int size) {
		Pageable pageable = PageRequest.of(0, size);
		List<Product> products = productRepository.findPopularProducts(pageable);
		return productConverter.toSimpleResponseList(products);
	}
}