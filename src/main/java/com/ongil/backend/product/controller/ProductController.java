package com.ongil.backend.product.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import com.ongil.backend.global.common.dto.DataResponse;
import com.ongil.backend.product.dto.request.ProductSearchCondition;
import com.ongil.backend.product.dto.response.ProductDetailResponse;
import com.ongil.backend.product.dto.response.ProductSimpleResponse;
import com.ongil.backend.product.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Product", description = "상품 관련 API")
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
		@RequestParam(required = false) Long category,
		@RequestParam(required = false) Long brand,
		@RequestParam(required = false) String priceRange,
		@RequestParam(required = false) String clothingSize,
		@PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		ProductSearchCondition condition = ProductSearchCondition.builder()
			.categoryId(category)
			.brandId(brand)
			.priceRange(priceRange)
			.size(clothingSize)
			.build();

		Page<ProductSimpleResponse> products = productService.getProducts(condition, pageable);

		return DataResponse.from(products);
	}

	@Operation(summary = "특가 상품 조회", description = "할인율이 높은 특가 상품 목록(10개)을 조회합니다.")
	@GetMapping("/special-sale")
	public DataResponse<Page<ProductSimpleResponse>> getSpecialSaleProducts(
		@PageableDefault(size = 20, sort = "discountRate", direction = Sort.Direction.DESC) Pageable pageable
	) {
		Page<ProductSimpleResponse> specialSaleProducts = productService.getSpecialSaleProducts(pageable);
		return DataResponse.from(specialSaleProducts);
	}

	@Operation(summary = "비슷한 상품 조회", description = "특정 상품과 비슷한 상품 최대 6개를 조회합니다.")
	@GetMapping("/{productId}/similar")
	public DataResponse<List<ProductSimpleResponse>> getSimilarProducts(@PathVariable Long productId) {
		List<ProductSimpleResponse> products = productService.getSimilarProducts(productId);
		return DataResponse.from(products);
	}
}
