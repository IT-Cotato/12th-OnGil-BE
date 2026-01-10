package com.ongil.backend.product.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.entity.ProductOption;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j  // ⬅️ 추가!
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

		log.info("===== 상품 조회 시작 =====");
		log.info("categoryId: {}", condition.getCategoryId());
		log.info("brandId: {}", condition.getBrandId());
		log.info("minPrice: {}, maxPrice: {}", minPrice, maxPrice);
		log.info("size: {}", condition.getSize());
		log.info("pageable: {}", pageable);

		Page<Product> products = productRepository.findAllByCondition(
			condition.getCategoryId(),
			condition.getBrandId(),
			minPrice,
			maxPrice,
			condition.getSize(),
			pageable
		);

		log.info("조회된 상품 수: {}", products.getTotalElements());
		log.info("조회된 상품 목록: {}", products.getContent().size());

		return products.map(productConverter::toSimpleResponse);
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