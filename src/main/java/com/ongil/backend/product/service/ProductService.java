package com.ongil.backend.product.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.entity.ProductOption;
import com.ongil.backend.domain.product.enums.ProductType;
import com.ongil.backend.domain.product.repository.ProductOptionRepository;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.product.dto.converter.ProductConverter;
import com.ongil.backend.product.dto.request.ProductSearchCondition;
import com.ongil.backend.product.dto.response.AiMaterialDescriptionResponse;
import com.ongil.backend.product.dto.response.ProductDetailResponse;
import com.ongil.backend.product.dto.response.ProductSimpleResponse;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final ProductOptionRepository productOptionRepository;
	private final ProductConverter productConverter;
	private final AiMaterialService aiMaterialService;

	@Transactional
	public ProductDetailResponse getProductDetail(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

		productRepository.incrementViewCount(productId);

		if (needsAiDescription(product)) {
			generateAndSaveAiDescription(product);
		}

		List<ProductOption> options = productOptionRepository.findByProductId(productId);

		return productConverter.toDetailResponse(product, options);
	}

	public Page<ProductSimpleResponse> getProducts(
		ProductSearchCondition condition,
		Pageable pageable
	) {
		Integer[] priceRange = condition.parsePriceRange();
		Integer minPrice = priceRange != null ? priceRange[0] : null;
		Integer maxPrice = priceRange != null ? priceRange[1] : null;

		Page<Product> products = productRepository.findAllByCondition(
			condition.getCategoryId(),
			condition.getBrandId(),
			minPrice,
			maxPrice,
			condition.getSize(),
			pageable
		);

		return products.map(productConverter::toSimpleResponse);
	}

	public Page<ProductSimpleResponse> getSpecialSaleProducts(Pageable pageable) {
		Page<Product> products = productRepository.findByOnSaleTrueAndProductTypeOrderByDiscountRateDesc(
			ProductType.SPECIAL_SALE,
			pageable
		);

		return products.map(productConverter::toSimpleResponse);
	}

	public List<ProductSimpleResponse> getSimilarProducts(Long productId) {
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

		int basePrice = product.getPrice();
		int minPrice = (int)(basePrice * 0.8);
		int maxPrice = (int)(basePrice * 1.2);

		Pageable pageable = PageRequest.of(0, 6);
		Page<Product> similarProducts = productRepository.findByOnSaleTrueAndCategoryIdAndIdNotAndPriceBetween(
			product.getCategory().getId(),
			productId,
			minPrice,
			maxPrice,
			pageable
		);

		return similarProducts.map(productConverter::toSimpleResponse).getContent();
	}

	private boolean needsAiDescription(Product product) {
		return product.getAiMaterialAdvantages() == null
			|| product.getAiMaterialDisadvantages() == null
			|| product.getAiMaterialCare() == null;
	}

	private void generateAndSaveAiDescription(Product product) {
		try {
			AiMaterialDescriptionResponse ai = aiMaterialService.generate(product.getMaterialOriginal());

			product.updateAiMaterialDescription(
				ai.getAdvantages(),
				ai.getDisadvantages(),
				ai.getCare()
			);

		} catch (Exception e) {
			AiMaterialDescriptionResponse defaultResponse = AiMaterialDescriptionResponse.createDefault();
			product.updateAiMaterialDescription(
				defaultResponse.getAdvantages(),
				defaultResponse.getDisadvantages(),
				defaultResponse.getCare()
			);
		}
	}
}