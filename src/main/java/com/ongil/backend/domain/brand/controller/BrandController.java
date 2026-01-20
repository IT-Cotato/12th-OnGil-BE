package com.ongil.backend.domain.brand.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.brand.dto.response.BrandResponse;
import com.ongil.backend.domain.brand.service.BrandService;
import com.ongil.backend.domain.product.dto.response.ProductSimpleResponse;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Brand", description = "브랜드 API")
@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

	private final BrandService brandService;

	@Operation(summary = "브랜드 목록 조회", description = "모든 브랜드를 가나다순으로 조회합니다.")
	@GetMapping
	public DataResponse<List<BrandResponse>> getAllBrands() {
		List<BrandResponse> brands = brandService.getAllBrands();
		return DataResponse.from(brands);
	}

	@Operation(summary = "브랜드 상세 조회", description = "특정 브랜드의 상세 정보를 조회합니다.")
	@GetMapping("/{brandId}")
	public DataResponse<BrandResponse> getBrandDetail(@PathVariable Long brandId) {
		BrandResponse brandDetail = brandService.getBrandDetail(brandId);
		return DataResponse.from(brandDetail);
	}

	@Operation(summary = "브랜드별 상품 목록 조회", description = "특정 브랜드의 상품 목록을 페이징하여 조회합니다.")
	@GetMapping("/{brandId}/products")
	public DataResponse<Page<ProductSimpleResponse>> getBrandProducts(
		@PathVariable Long brandId,
		@PageableDefault(size = 20)
		Pageable pageable
	) {
		Page<ProductSimpleResponse> products = brandService.getBrandProducts(brandId, pageable);
		return DataResponse.from(products);
	}
}
