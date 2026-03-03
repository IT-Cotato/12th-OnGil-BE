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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

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

		if (cached != null && !cached.isEmpty()) {
			return cached;
		}

		// Cache Miss → DB 조회
		List<Brand> brands = brandRepository.findAllOrderByName();
		List<BrandResponse> response = brandConverter.toResponseList(brands);

		if (response.isEmpty()) {
			log.warn("조회된 브랜드가 없습니다.");
			return response;
		}

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

	// 추천 브랜드 조회 (랜덤 3개 브랜드 + 각 브랜드별 6개 상품)
	public List<BrandRecommendResponse> getRecommendBrands() {
		List<Brand> randomBrands = brandRepository.findRandomBrands();

		return randomBrands.stream()
			.map(brand -> {
				List<Product> products = productRepository.findRandomProductsByBrandId(brand.getId(), 6);
				List<ProductSimpleResponse> productResponses = products.stream()
					.map(productConverter::toSimpleResponse)
					.collect(Collectors.toList());

				return BrandRecommendResponse.builder()
					.id(brand.getId())
					.name(brand.getName())
					.logoImageUrl(brand.getLogoImageUrl())
					.products(productResponses)
					.build();
			})
			.collect(Collectors.toList());
	}
}