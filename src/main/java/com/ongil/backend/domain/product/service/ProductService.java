package com.ongil.backend.domain.product.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.product.converter.ProductConverter;
import com.ongil.backend.domain.product.dto.request.ProductSearchCondition;
import com.ongil.backend.domain.product.dto.response.AiMaterialDescriptionResponse;
import com.ongil.backend.domain.product.dto.response.ProductDetailResponse;
import com.ongil.backend.domain.product.dto.response.ProductSimpleResponse;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.entity.ProductOption;
import com.ongil.backend.domain.product.enums.ProductSortType;
import com.ongil.backend.domain.product.enums.ProductType;
import com.ongil.backend.domain.product.repository.ProductOptionRepository;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductOptionRepository productOptionRepository;
	private final ProductConverter productConverter;
	private final AiMaterialService aiMaterialService;

	// 상품 상세 조회
	@Transactional
	public ProductDetailResponse getProductDetail(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

		productRepository.incrementViewCount(productId);

		if (needsAiDescription(product)) {
			generateAndSaveAiDescription(product);
		}

		List<ProductOption> options = productOptionRepository.findByProductId(productId);

		return productConverter.toDetailResponse(product, options);
	}

	// 조건에 따른 상품 조회
	public Page<ProductSimpleResponse> getProducts(
		ProductSearchCondition condition,
		ProductSortType sortType,
		Pageable pageable
	) {
		Integer[] priceRange = condition.parsePriceRange();
		Integer minPrice = priceRange != null ? priceRange[0] : null;
		Integer maxPrice = priceRange != null ? priceRange[1] : null;

		// 정렬 조건 생성
		Sort sort = createSort(sortType);
		Pageable pageableWithSort = PageRequest.of(
			pageable.getPageNumber(),
			pageable.getPageSize(),
			sort
		);

		Page<Product> products = productRepository.findAllByCondition(
			condition.getCategoryId(),
			condition.getBrandId(),
			minPrice,
			maxPrice,
			condition.getSize(),
			pageableWithSort
		);

		return products.map(productConverter::toSimpleResponse);
	}

	// 특가 상품 조회
	public List<ProductSimpleResponse> getSpecialSaleProducts() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "discountRate"));
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
}