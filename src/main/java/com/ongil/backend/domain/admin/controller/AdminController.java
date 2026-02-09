package com.ongil.backend.domain.admin.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.admin.dto.request.AdminBrandCreateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminBrandUpdateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminCategoryCreateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminCategoryUpdateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminProductCreateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminProductOptionCreateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminProductOptionUpdateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminProductUpdateRequest;
import com.ongil.backend.domain.admin.service.AdminService;
import com.ongil.backend.domain.brand.dto.response.BrandResponse;
import com.ongil.backend.domain.category.dto.response.CategorySimpleResponse;
import com.ongil.backend.domain.product.dto.response.ProductOptionResponse;
import com.ongil.backend.domain.product.dto.response.ProductSimpleResponse;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Admin", description = "관리자 API (Swagger 테스트용)")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	@Operation(summary = "브랜드 등록", description = "새로운 브랜드를 등록합니다.")
	@PostMapping("/brands")
	public DataResponse<BrandResponse> createBrand(@Valid @RequestBody AdminBrandCreateRequest request) {
		BrandResponse response = adminService.createBrand(request);
		return DataResponse.from(response);
	}

	@Operation(summary = "카테고리 등록", description = "새로운 카테고리를 등록합니다. 상위 카테고리 ID를 입력하면 하위 카테고리로 등록됩니다.")
	@PostMapping("/categories")
	public DataResponse<CategorySimpleResponse> createCategory(@Valid @RequestBody AdminCategoryCreateRequest request) {
		CategorySimpleResponse response = adminService.createCategory(request);
		return DataResponse.from(response);
	}

	@Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.")
	@PostMapping("/products")
	public DataResponse<ProductSimpleResponse> createProduct(@Valid @RequestBody AdminProductCreateRequest request) {
		ProductSimpleResponse response = adminService.createProduct(request);
		return DataResponse.from(response);
	}

	@Operation(summary = "상품 옵션 등록", description = "상품의 옵션(사이즈, 색상, 재고)을 등록합니다.")
	@PostMapping("/product-options")
	public DataResponse<ProductOptionResponse> createProductOption(
		@Valid @RequestBody AdminProductOptionCreateRequest request) {
		ProductOptionResponse response = adminService.createProductOption(request);
		return DataResponse.from(response);
	}

	@Operation(summary = "브랜드 수정", description = "브랜드 정보를 수정합니다. 수정할 필드만 입력하면 됩니다.")
	@PatchMapping("/brands/{brandId}")
	public DataResponse<BrandResponse> updateBrand(
		@PathVariable Long brandId,
		@RequestBody AdminBrandUpdateRequest request) {
		BrandResponse response = adminService.updateBrand(brandId, request);
		return DataResponse.from(response);
	}

	@Operation(summary = "브랜드 삭제", description = "브랜드를 삭제합니다.")
	@DeleteMapping("/brands/{brandId}")
	public DataResponse<String> deleteBrand(@PathVariable Long brandId) {
		adminService.deleteBrand(brandId);
		return DataResponse.from("브랜드가 삭제되었습니다.");
	}

	@Operation(summary = "카테고리 수정", description = "카테고리 정보를 수정합니다. 수정할 필드만 입력하면 됩니다.")
	@PatchMapping("/categories/{categoryId}")
	public DataResponse<CategorySimpleResponse> updateCategory(
		@PathVariable Long categoryId,
		@RequestBody AdminCategoryUpdateRequest request) {
		CategorySimpleResponse response = adminService.updateCategory(categoryId, request);
		return DataResponse.from(response);
	}

	@Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다.")
	@DeleteMapping("/categories/{categoryId}")
	public DataResponse<String> deleteCategory(@PathVariable Long categoryId) {
		adminService.deleteCategory(categoryId);
		return DataResponse.from("카테고리가 삭제되었습니다.");
	}

	@Operation(summary = "상품 수정", description = "상품 정보를 수정합니다. 수정할 필드만 입력하면 됩니다.")
	@PatchMapping("/products/{productId}")
	public DataResponse<ProductSimpleResponse> updateProduct(
		@PathVariable Long productId,
		@RequestBody AdminProductUpdateRequest request) {
		ProductSimpleResponse response = adminService.updateProduct(productId, request);
		return DataResponse.from(response);
	}

	@Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
	@DeleteMapping("/products/{productId}")
	public DataResponse<String> deleteProduct(@PathVariable Long productId) {
		adminService.deleteProduct(productId);
		return DataResponse.from("상품이 삭제되었습니다.");
	}

	@Operation(summary = "상품 옵션 수정", description = "상품 옵션 정보를 수정합니다. 수정할 필드만 입력하면 됩니다.")
	@PatchMapping("/product-options/{optionId}")
	public DataResponse<ProductOptionResponse> updateProductOption(
		@PathVariable Long optionId,
		@RequestBody AdminProductOptionUpdateRequest request) {
		ProductOptionResponse response = adminService.updateProductOption(optionId, request);
		return DataResponse.from(response);
	}

	@Operation(summary = "상품 옵션 삭제", description = "상품 옵션을 삭제합니다.")
	@DeleteMapping("/product-options/{optionId}")
	public DataResponse<String> deleteProductOption(@PathVariable Long optionId) {
		adminService.deleteProductOption(optionId);
		return DataResponse.from("상품 옵션이 삭제되었습니다.");
	}
}
