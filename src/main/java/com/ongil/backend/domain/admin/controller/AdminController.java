package com.ongil.backend.domain.admin.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.admin.dto.request.AdminBrandCreateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminCategoryCreateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminProductCreateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminProductOptionCreateRequest;
import com.ongil.backend.domain.admin.service.AdminService;
import com.ongil.backend.domain.brand.dto.response.BrandResponse;
import com.ongil.backend.domain.category.dto.response.CategorySimpleResponse;
import com.ongil.backend.domain.product.dto.response.ProductOptionResponse;
import com.ongil.backend.domain.product.dto.response.ProductSimpleResponse;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin", description = "관리자 API (Swagger 테스트용)")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	@Operation(summary = "브랜드 등록", description = "새로운 브랜드를 등록합니다.")
	@PostMapping("/brands")
	public DataResponse<BrandResponse> createBrand(@RequestBody AdminBrandCreateRequest request) {
		BrandResponse response = adminService.createBrand(request);
		return DataResponse.from(response);
	}

	@Operation(summary = "카테고리 등록", description = "새로운 카테고리를 등록합니다. 상위 카테고리 ID를 입력하면 하위 카테고리로 등록됩니다.")
	@PostMapping("/categories")
	public DataResponse<CategorySimpleResponse> createCategory(@RequestBody AdminCategoryCreateRequest request) {
		CategorySimpleResponse response = adminService.createCategory(request);
		return DataResponse.from(response);
	}

	@Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.")
	@PostMapping("/products")
	public DataResponse<ProductSimpleResponse> createProduct(@RequestBody AdminProductCreateRequest request) {
		ProductSimpleResponse response = adminService.createProduct(request);
		return DataResponse.from(response);
	}

	@Operation(summary = "상품 옵션 등록", description = "상품의 옵션(사이즈, 색상, 재고)을 등록합니다.")
	@PostMapping("/product-options")
	public DataResponse<ProductOptionResponse> createProductOption(
		@RequestBody AdminProductOptionCreateRequest request) {
		ProductOptionResponse response = adminService.createProductOption(request);
		return DataResponse.from(response);
	}
}
