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
import com.ongil.backend.domain.product.dto.response.ProductOptionResponse;
import com.ongil.backend.domain.product.dto.response.ProductSearchPageResDto;
import com.ongil.backend.domain.product.dto.response.ProductSimpleResponse;

import com.ongil.backend.domain.product.dto.response.SizeGuideResponse;
import com.ongil.backend.domain.product.enums.ProductSortType;
import com.ongil.backend.domain.product.service.ProductService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

	@Operation(summary = "상품 상세 조회", description = "상품의 상세 정보를 조회합니다. 로그인 사용자의 경우 조회 기록이 저장됩니다.")
	@GetMapping("/{productId}")
	public DataResponse<ProductDetailResponse> getProductDetail(
		@PathVariable Long productId,
		@AuthenticationPrincipal Long userId
	) {
		ProductDetailResponse productDetail = productService.getProductDetail(productId, userId);
		return DataResponse.from(productDetail);
	}

	@Operation(summary = "상품별 전체 옵션 목록 조회", description = "상품의 색상/사이즈별 옵션과 재고 정보를 조회합니다. 장바구니 추가 시 사용됩니다.")
	@GetMapping("/{productId}/options")
	public DataResponse<List<ProductOptionResponse>> getProductOptions(
		@PathVariable Long productId
	) {
		List<ProductOptionResponse> options = productService.getProductOptions(productId);
		return DataResponse.from(options);
	}

	@Operation(
		summary = "상품 목록 조회 (검색 포함)",
		description = """
    조건에 맞는 상품 목록을 조회합니다.
    - query가 없는 경우: 일반 상품 목록 조회(카테고리/브랜드/가격/사이즈 필터 + 정렬 + 페이징)
    - query가 있는 경우: Elasticsearch로 검색어에 매칭되는 상품 ID를 먼저 조회한 뒤,
      해당 ID 범위 내에서 필터/정렬/페이징을 적용하여 '검색 결과 목록'을 반환합니다.
    - 검색 결과가 0개인 경우: products는 빈 페이지로 반환하고, alternatives(최대 4개 대체 검색어)를 함께 반환합니다.
    - 로그인 사용자(userId 존재)인 경우: 검색 성공 시 최근검색어/검색 로그가 저장됩니다.
  """
	)
	@GetMapping
	public DataResponse<ProductSearchPageResDto> getProducts(
		@RequestParam(required = false) String query,
		@RequestParam(required = false) Long categoryId,
		@RequestParam(required = false) List<Long> brandIds,
		@RequestParam(required = false) String priceRange,
		@RequestParam(required = false) List<String> clothingSizes,
		@RequestParam(required = false, defaultValue = "POPULAR") ProductSortType sortType,
		@PageableDefault(size = 20) Pageable pageable,
		@AuthenticationPrincipal Long userId
	) {
		ProductSearchCondition condition = ProductSearchCondition.builder()
			.categoryId(categoryId)
			.brandIds(brandIds != null && !brandIds.isEmpty() ? brandIds : null)
			.priceRange(priceRange)
			.sizes(clothingSizes != null && !clothingSizes.isEmpty() ? clothingSizes : null)
			.build();

		ProductSearchPageResDto res = productService.getProducts(condition, sortType, pageable, query, userId);
		return DataResponse.from(res);
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

	@Operation(summary = "사이즈 가이드 조회", description = "유사 체형 고객의 구매 데이터를 기반으로 사이즈를 추천합니다. (토큰 필요)")
	@GetMapping("/{productId}/size-guide")
	public DataResponse<SizeGuideResponse> getSizeGuide(
		@PathVariable Long productId, @AuthenticationPrincipal Long userId) {
		SizeGuideResponse responses = productService.getSizeGuide(productId, userId);
		return DataResponse.from(responses);
	}

	@Operation(
		summary = "홈화면 추천 상품 조회",
		description = """
			로그인 사용자: 최근 30일간 조회/장바구니 상품 기준 같은 카테고리 + 비슷한 가격대(±10,000원) 상품 추천
			비로그인 사용자: 전체 인기 상품 추천

			정렬 기준: 전체 고객의 조회수 + 장바구니 담긴 횟수 순
			"""
	)
	@GetMapping("/recommend")
	public DataResponse<List<ProductSimpleResponse>> getRecommendedProducts(
		@RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
	) {
		List<ProductSimpleResponse> products = productService.getRecommendedProducts(size);
		return DataResponse.from(products);
	}
}