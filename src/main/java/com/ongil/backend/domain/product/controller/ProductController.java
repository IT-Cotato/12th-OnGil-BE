package com.ongil.backend.domain.product.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ongil.backend.domain.product.dto.request.ProductSearchCondition;
import com.ongil.backend.domain.product.dto.response.ProductDetailResponse;
import com.ongil.backend.domain.product.dto.response.ProductSimpleResponse;
import com.ongil.backend.domain.product.dto.response.SizeGuideResponse;
import com.ongil.backend.domain.product.enums.ProductSortType;
import com.ongil.backend.domain.product.service.ProductService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "Product", description = "상품 관련 API")
@Validated
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@Operation(summary = "상품 상세 조회", description = "상품의 상세 정보를 조회합니다.")
	@GetMapping("/{productId}")
	public DataResponse<ProductDetailResponse> getProductDetail(@PathVariable Long productId) {
		ProductDetailResponse productDetail = productService.getProductDetail(productId);
		return DataResponse.from(productDetail);
	}

	@Operation(summary = "상품 목록 조회", description = "조건에 맞는 상품들의 목록을 조회합니다.")
	@GetMapping
	public DataResponse<Page<ProductSimpleResponse>> getProducts(
		@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false) Long brandId,
		@RequestParam(required = false) String priceRange,
		@RequestParam(required = false) String clothingSize,
		@RequestParam(required = false, defaultValue = "POPULAR") ProductSortType sortType,
		@PageableDefault(size = 20) Pageable pageable
	) {
		ProductSearchCondition condition = ProductSearchCondition.builder()
			.categoryId(categoryId)
			.brandId(brandId)
			.priceRange(priceRange)
			.size(clothingSize)
			.build();

		Page<ProductSimpleResponse> products = productService.getProducts(condition, sortType, pageable);

		return DataResponse.from(products);
	}

	@Operation(summary = "특가 상품 조회", description = "할인율이 높은 특가 상품 TOP 10을 조회합니다.")
	@GetMapping("/special-sale")
	public DataResponse<List<ProductSimpleResponse>> getSpecialSaleProducts() {
		List<ProductSimpleResponse> products = productService.getSpecialSaleProducts();
		return DataResponse.from(products);
	}

	@Operation(summary = "비슷한 상품 조회", description = "특정 상품과 비슷한 상품 최대 6개를 조회합니다.")
	@GetMapping("/{productId}/similar")
	public DataResponse<List<ProductSimpleResponse>> getSimilarProducts(@PathVariable Long productId) {
		List<ProductSimpleResponse> products = productService.getSimilarProducts(productId);
		return DataResponse.from(products);
	}

	@Operation(summary = "상품 검색 (음성/텍스트)", description = "키워드로 상품을 검색합니다. searchType 파라미터가 없으면 기본 TEXT로 동작합니다.")
	@GetMapping("/search")
	public DataResponse<Page<ProductSimpleResponse>> searchProducts(
			@RequestParam
			@NotBlank(message = "검색 키워드는 필수입니다")
			@Size(min = 1, max = 50, message = "검색 키워드는 1-50자 이내여야 합니다")
			String keyword,

			// required = false 덕분에 기존 코드는 이 값을 안 보내도 에러가 안 납니다.
			@RequestParam(required = false, defaultValue = "TEXT")
			String searchType,

			@PageableDefault(size = 20) Pageable pageable
	) {
		// 로그 기록 (음성 검색인지 확인용)
		if ("VOICE".equalsIgnoreCase(searchType)) {
			log.info("🎤 음성 검색 요청 - keyword: {}", keyword);
		} else {
			log.debug("텍스트 검색 요청 - keyword: {}", keyword);
		}

		// 서비스 로직은 기존 그대로 호출
		Page<ProductSimpleResponse> products = productService.searchProducts(keyword, pageable);
		return DataResponse.from(products);
	}

	@Operation(summary = "사이즈 가이드 조회", description = "유사 체형 고객의 구매 데이터를 기반으로 사이즈를 추천합니다.")
	@GetMapping("/{productId}/size-guide")
	public DataResponse<SizeGuideResponse> getSizeGuide(
		@PathVariable Long productId, @AuthenticationPrincipal Long userId) {
		SizeGuideResponse responses = productService.getSizeGuide(productId, userId);
		return DataResponse.from(responses);
	}
}