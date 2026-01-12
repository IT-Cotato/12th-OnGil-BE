package com.ongil.backend.brand.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.brand.dto.converter.BrandConverter;
import com.ongil.backend.brand.dto.response.BrandResponse;
import com.ongil.backend.domain.brand.entity.Brand;
import com.ongil.backend.domain.brand.repository.BrandRepository;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.product.dto.converter.ProductConverter;
import com.ongil.backend.product.dto.response.ProductSimpleResponse;

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
}
