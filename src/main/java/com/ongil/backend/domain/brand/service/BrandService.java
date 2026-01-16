package com.ongil.backend.domain.brand.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.brand.dto.response.BrandRecommendResponse;
import com.ongil.backend.domain.brand.converter.BrandConverter;
import com.ongil.backend.domain.brand.dto.response.BrandResponse;
import com.ongil.backend.domain.brand.entity.Brand;
import com.ongil.backend.domain.brand.repository.BrandRepository;
import com.ongil.backend.domain.product.converter.ProductConverter;
import com.ongil.backend.domain.product.dto.response.ProductSimpleResponse;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

	private final BrandRepository brandRepository;
	private final ProductRepository productRepository;
	private final BrandConverter brandConverter;
	private final ProductConverter productConverter;

	public List<BrandResponse> getAllBrands() {
		List<Brand> brands = brandRepository.findAllOrderByName();
		return brandConverter.toResponseList(brands);
	}

	public BrandResponse getBrandDetail(Long brandId) {
		Brand brand = brandRepository.findById(brandId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND));

		return brandConverter.toResponse(brand);
	}

	public Page<ProductSimpleResponse> getBrandProducts(Long brandId, Pageable pageable) {
		Brand brand = brandRepository.findById(brandId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND));

		Page<Product> products = productRepository.findByBrandId(brand.getId(), pageable);

		return products.map(productConverter::toSimpleResponse);
	}

	public List<BrandRecommendResponse> getRecommendBrands() {
		// 1. 랜덤 브랜드 3개 가져오기
		List<Brand> randomBrands = brandRepository.findRandomBrands();

		// 2. 각 브랜드별로 상품 6개 가져와서 DTO로 변환
		return randomBrands.stream().map(brand -> {

			// 2-1. 해당 브랜드의 랜덤 상품 6개 조회
			List<Product> randomProducts = productRepository.findRandomProductsByBrand(brand.getId());

			// 2-2. 상품들을 DTO로 변환 (ProductConverter 활용)
			List<ProductSimpleResponse> productDtos = randomProducts.stream()
					.map(productConverter::toSimpleResponse)
					.toList();

			// 2-3. 최종 추천 응답 DTO 생성
			return BrandRecommendResponse.builder()
					.id(brand.getId())
					.name(brand.getName())
					.logoImageUrl(brand.getLogoImageUrl()) // ※ Entity 변수명(logoUrl vs logoImageUrl) 확인 필요!
					.products(productDtos)
					.build();
		}).toList();
	}
}
