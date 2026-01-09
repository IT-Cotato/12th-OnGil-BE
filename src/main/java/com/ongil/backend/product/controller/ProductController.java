package com.ongil.backend.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.global.common.dto.DataResponse;
import com.ongil.backend.product.dto.response.ProductDetailResponse;
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
}
