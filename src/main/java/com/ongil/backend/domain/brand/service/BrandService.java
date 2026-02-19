package com.ongil.backend.domain.brand.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.brand.converter.BrandConverter;
import com.ongil.backend.domain.brand.dto.response.BrandRecommendResponse;
import com.ongil.backend.domain.brand.dto.response.BrandResponse;
import com.ongil.backend.domain.brand.entity.Brand;
import com.ongil.backend.domain.brand.repository.BrandRepository;
import com.ongil.backend.domain.product.converter.ProductConverter;
import com.ongil.backend.domain.product.dto.response.ProductSimpleResponse;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.config.redis.CacheKeyConstants;
import com.ongil.backend.global.config.redis.RedisCacheService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

	private static final int BRAND_PRODUCT_COUNT = 6;

	private final BrandRepository brandRepository;
	private final ProductRepository productRepository;
	private final BrandConverter brandConverter;
	private final ProductConverter productConverter;
	private final RedisCacheService redisCacheService;

	// 브랜드 전체 조회
	public List<BrandResponse> getAllBrands() {
		// Redis 캐시 확인
		List<BrandResponse> cached = redisCacheService.getList(
			CacheKeyConstants.BRANDS_ALL,
			BrandResponse.class
		);

		if (cached != null) {
			return cached;
		}

		// Cache Miss → DB 조회
		List<Brand> brands = brandRepository.findAllOrderByName();
		List<BrandResponse> response = brandConverter.toResponseList(brands);

		// Redis 캐싱 (무한 TTL)
		redisCacheService.save(
			CacheKeyConstants.BRANDS_ALL,
			response,
			CacheKeyConstants.MASTER_DATA_TTL_HOURS
		);

		return response;
	}

	// 브랜드 상세 조회
	public BrandResponse getBrandDetail(Long brandId) {
		Brand brand = brandRepository.findById(brandId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND));

		return brandConverter.toResponse(brand);
	}

	// 브랜드별 상품 조회
	public Page<ProductSimpleResponse> getBrandProducts(Long brandId, Pageable pageable) {
		if (!brandRepository.existsById(brandId)) {
			throw new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND);
		}

		Page<Product> products = productRepository.findByBrandId(brandId, pageable);
		return products.map(productConverter::toSimpleResponse);
	}

	/**
	 * 홈 화면 추천 브랜드 조회
	 * 랜덤으로 선택된 브랜드 3개와 각 브랜드별 상품 6개를 반환
	 */
	public List<BrandRecommendResponse> getRecommendBrands() {
		List<Brand> randomBrands = brandRepository.findRandomBrands();

		return randomBrands.stream()
			.map(this::buildBrandRecommendResponse)
			.collect(Collectors.toList());
	}

	/**
	 * 브랜드 추천 응답 객체 생성
	 * 브랜드 정보와 해당 브랜드의 상품 목록을 조합하여 응답 객체를 생성
	 */
	private BrandRecommendResponse buildBrandRecommendResponse(Brand brand) {
		List<ProductSimpleResponse> productResponses = fetchBrandProducts(brand.getId());

		return BrandRecommendResponse.builder()
			.id(brand.getId())
			.name(brand.getName())
			.logoImageUrl(brand.getLogoImageUrl())
			.products(productResponses)
			.build();
	}

	/**
	 * 브랜드별 랜덤 상품 조회
	 * 지정된 개수만큼 랜덤 상품을 조회하여 간단한 응답 형태로 변환
	 */
	private List<ProductSimpleResponse> fetchBrandProducts(Long brandId) {
		List<Product> products = productRepository.findRandomProductsByBrandId(brandId, BRAND_PRODUCT_COUNT);
		
		return products.stream()
			.map(productConverter::toSimpleResponse)
			.collect(Collectors.toList());
	}
}