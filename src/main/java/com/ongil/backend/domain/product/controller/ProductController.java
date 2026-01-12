package com.ongil.backend.domain.product.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.ongil.backend.domain.product.dto.request.ProductSearchCondition;
import com.ongil.backend.domain.product.dto.response.ProductDetailResponse;
import com.ongil.backend.domain.product.dto.response.ProductSimpleResponse;
import com.ongil.backend.domain.product.enums.ProductSortType;
import com.ongil.backend.domain.product.service.ProductService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

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

	@Operation(summary = "상품 검색", description = "키워드로 상품을 검색합니다. (브랜드명, 카테고리명, 색상, 상품명)")
	@GetMapping("/search")
	public DataResponse<Page<ProductSimpleResponse>> searchProducts(
		@RequestParam
		@NotBlank(message = "검색 키워드는 필수입니다")
		@Size(min = 1, max = 50, message = "검색 키워드는 1-50자 이내여야 합니다")
		String keyword,
		@PageableDefault(size = 20) Pageable pageable
	) {
		Page<ProductSimpleResponse> products = productService.searchProducts(keyword, pageable);
		return DataResponse.from(products);
	}
}