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
		Page<Product> products = productRepository.findByBrandId(brandId, pageable);
		return products.map(productConverter::toSimpleResponse);
	}
}
