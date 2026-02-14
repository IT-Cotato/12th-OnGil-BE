package com.ongil.backend.domain.admin.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.admin.dto.request.AdminBrandCreateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminBrandUpdateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminCategoryCreateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminCategoryUpdateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminProductCreateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminProductOptionCreateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminProductOptionUpdateRequest;
import com.ongil.backend.domain.admin.dto.request.AdminProductUpdateRequest;
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
import com.ongil.backend.domain.product.entity.ProductOption;
import com.ongil.backend.domain.product.enums.ProductType;
import com.ongil.backend.domain.product.repository.ProductOptionRepository;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.common.exception.ValidationException;

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

		// 상품은 하위 카테고리에만 등록 가능
		if (category.getParentCategory() == null) {
			throw new ValidationException(ErrorCode.INVALID_CATEGORY);
		}

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

	// 브랜드 수정
	public BrandResponse updateBrand(Long brandId, AdminBrandUpdateRequest request) {
		Brand brand = brandRepository.findById(brandId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND));

		brand.updateBrand(
			request.getName(),
			request.getDescription(),
			request.getLogoImageUrl()
		);

		return brandConverter.toResponse(brand);
	}

	// 브랜드 삭제
	public void deleteBrand(Long brandId) {
		Brand brand = brandRepository.findById(brandId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND));

		// 해당 브랜드를 사용하는 상품이 있는지 확인
		if (productRepository.existsByBrandId(brandId)) {
			throw new ValidationException(ErrorCode.CANNOT_DELETE_BRAND_WITH_PRODUCTS);
		}

		brandRepository.delete(brand);
	}

	// 카테고리 수정
	public CategorySimpleResponse updateCategory(Long categoryId, AdminCategoryUpdateRequest request) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));

		Category parentCategory = null;
		if (request.getParentCategoryId() != null) {
			parentCategory = categoryRepository.findById(request.getParentCategoryId())
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));
		}

		category.updateCategory(
			request.getName(),
			request.getIconUrl(),
			request.getDisplayOrder(),
			parentCategory,
			null  // sizeChartType은 수정하지 않음
		);

		return categoryConverter.toSimpleResponse(category);
	}

	// 카테고리 삭제
	public void deleteCategory(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));

		// 해당 카테고리를 사용하는 상품이 있는지 확인
		if (productRepository.existsByCategoryId(categoryId)) {
			throw new ValidationException(ErrorCode.CANNOT_DELETE_CATEGORY_WITH_PRODUCTS);
		}

		// 하위 카테고리가 있는지 확인
		if (categoryRepository.existsByParentCategoryId(categoryId)) {
			throw new ValidationException(ErrorCode.CANNOT_DELETE_CATEGORY_WITH_SUBCATEGORIES);
		}

		categoryRepository.delete(category);
	}

	// 상품 수정
	public ProductSimpleResponse updateProduct(Long productId, AdminProductUpdateRequest request) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

		Brand brand = null;
		if (request.getBrandId() != null) {
			brand = brandRepository.findById(request.getBrandId())
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.BRAND_NOT_FOUND));
		}

		Category category = null;
		if (request.getCategoryId() != null) {
			category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND));

			// 상품은 하위 카테고리에만 등록 가능
			if (category.getParentCategory() == null) {
				throw new ValidationException(ErrorCode.INVALID_CATEGORY);
			}
		}

		product.updateProduct(
			request.getName(),
			request.getDescription(),
			request.getPrice(),
			request.getMaterialOriginal(),
			request.getImageUrls(),
			request.getSizes(),
			request.getColors(),
			request.getDiscountRate(),
			request.getProductType(),
			brand,
			category
		);

		return productConverter.toSimpleResponse(product);
	}

	// 상품 옵션 수정
	public ProductOptionResponse updateProductOption(Long optionId, AdminProductOptionUpdateRequest request) {
		ProductOption productOption = productOptionRepository.findById(optionId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_OPTION_NOT_FOUND));

		productOption.updateProductOption(
			request.getSize(),
			request.getColor(),
			request.getStock()
		);

		return ProductOptionResponse.builder()
			.optionId(productOption.getId())
			.size(productOption.getSize())
			.color(productOption.getColor())
			.stock(productOption.getStock())
			.stockStatus(productOption.getStockStatus())
			.build();
	}

	// 상품 옵션 삭제
	public void deleteProductOption(Long optionId) {
		ProductOption productOption = productOptionRepository.findById(optionId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_OPTION_NOT_FOUND));

		productOptionRepository.delete(productOption);
	}
}
