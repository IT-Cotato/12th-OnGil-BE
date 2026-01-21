package com.ongil.backend.domain.brand.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.ongil.backend.global.config.redis.CacheKeyConstants;
import com.ongil.backend.global.config.redis.RedisCacheService;

import lombok.RequiredArgsConstructor;

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
}