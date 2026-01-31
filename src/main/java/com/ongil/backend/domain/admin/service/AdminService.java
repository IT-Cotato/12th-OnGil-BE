package com.ongil.backend.domain.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.admin.dto.request.AdminBrandCreateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminCategoryCreateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminProductCreateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminProductOptionCreateRequest;
import com.ongil.backend.domain.brand.converter.BrandConverter;
import com.ongil.backend.domain.brand.dto.response.BrandResponse;
import com.ongil.backend.domain.brand.entity.Brand;
import com.ongil.backend.domain.brand.repository.BrandRepository;
import com.ongil.backend.domain.category.converter.CategoryConverter;
import com.ongil.backend.domain.category.dto.response.CategorySimpleResponse;
import com.ongil.backend.domain.category.entity.Category;
import com.ongil.backend.domain.category.repository.CategoryRepository;
import com.ongil.backend.domain.product.converter.ProductConverter;
import com.ongil.backend.domain.product.dto.response.ProductOptionResponse;
import com.ongil.backend.domain.product.dto.response.ProductSimpleResponse;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.enums.ProductType;
import com.ongil.backend.domain.product.entity.ProductOption;
import com.ongil.backend.domain.product.repository.ProductOptionRepository;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

	private final BrandRepository brandRepository;
	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;
	private final ProductOptionRepository productOptionRepository;
	private final BrandConverter brandConverter;
	private final CategoryConverter categoryConverter;
	private final ProductConverter productConverter;

	public BrandResponse createBrand(AdminBrandCreateRequest request) {
		Brand brand = Brand.builder()
			.name(request.getName())
			.description(request.getDescription())
			.logoImageUrl(request.getLogoImageUrl())
			.build();

		Brand savedBrand = brandRepository.save(brand);
		return brandConverter.toResponse(savedBrand);
	}

	public CategorySimpleResponse createCategory(AdminCategoryCreateRequest request) {
		Category parentCategory = null;
		if (request.getParentCategoryId() != null) {
			parentCategory = categoryRepository.findById(request.getParentCategoryId())
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));
		}

		Category category = Category.builder()
			.name(request.getName())
			.iconUrl(request.getIconUrl())
			.displayOrder(request.getDisplayOrder())
			.parentCategory(parentCategory)
			.build();

		Category savedCategory = categoryRepository.save(category);
		return categoryConverter.toSimpleResponse(savedCategory);
	}

	public ProductSimpleResponse createProduct(AdminProductCreateRequest request) {
		Brand brand = brandRepository.findById(request.getBrandId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND));

		Category category = categoryRepository.findById(request.getCategoryId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));

		Integer discountPrice = null;
		if (request.getPrice() != null && request.getDiscountRate() != null && request.getDiscountRate() > 0) {
			discountPrice = request.getPrice() - (request.getPrice() * request.getDiscountRate() / 100);
		}

		Product product = Product.builder()
			.name(request.getName())
			.description(request.getDescription())
			.price(request.getPrice())
			.materialOriginal(request.getMaterialOriginal())
			.imageUrls(request.getImageUrls())
			.sizes(request.getSizes())
			.colors(request.getColors())
			.discountRate(request.getDiscountRate())
			.discountPrice(discountPrice)
			.productType(request.getProductType() != null ? request.getProductType() : ProductType.NORMAL)
			.brand(brand)
			.category(category)
			.build();

		Product savedProduct = productRepository.save(product);
		return productConverter.toSimpleResponse(savedProduct);
	}

	public ProductOptionResponse createProductOption(AdminProductOptionCreateRequest request) {
		Product product = productRepository.findById(request.getProductId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

		ProductOption productOption = ProductOption.builder()
			.product(product)
			.size(request.getSize())
			.color(request.getColor())
			.stock(request.getStock())
			.build();

		ProductOption savedOption = productOptionRepository.save(productOption);
		return ProductOptionResponse.builder()
			.optionId(savedOption.getId())
			.size(savedOption.getSize())
			.color(savedOption.getColor())
			.stock(savedOption.getStock())
			.stockStatus(savedOption.getStockStatus())
			.build();
	}
}
