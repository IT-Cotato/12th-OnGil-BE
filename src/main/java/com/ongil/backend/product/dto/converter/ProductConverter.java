package com.ongil.backend.product.dto.converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.entity.ProductOption;
import com.ongil.backend.product.dto.response.ProductDetailResponse;
import com.ongil.backend.product.dto.response.ProductOptionResponse;
import com.ongil.backend.product.dto.response.ProductSimpleResponse;

@Component
public class ProductConverter {

	// 상품 상세 응답 변환
	public ProductDetailResponse toDetailResponse(Product product, List<ProductOption> options) {
		return ProductDetailResponse.builder()
			.id(product.getId())
			.name(product.getName())
			.description(product.getDescription())
			.originalPrice(product.getPrice())
			.discountRate(product.getDiscountRate())
			.discountPrice(product.getDiscountPrice())
			.finalPrice(calculateFinalPrice(product))
			.materialOriginal(product.getMaterialOriginal())
			.materialDescription(convertMaterialDescription(product))
			.washingMethod(product.getWashingMethod())
			.sizes(parseStringToList(product.getSizes()))
			.colors(parseStringToList(product.getColors()))
			.options(convertOptions(options))
			.imageUrls(parseStringToList(product.getImageUrls()))
			.brandId(product.getBrand().getId())
			.brandName(product.getBrand().getName())
			.categoryId(product.getCategory().getId())
			.categoryName(product.getCategory().getName())
			.viewCount(product.getViewCount())
			.purchaseCount(product.getPurchaseCount())
			.onSale(product.getOnSale())
			.productType(product.getProductType())
			.productTypeDescription(product.getProductType().getDescription())
			.build();
	}

	// 상품 간단 응답 변환
	public ProductSimpleResponse toSimpleResponse(Product product) {
		return ProductSimpleResponse.builder()
			.id(product.getId())
			.name(product.getName())
			.originalPrice(product.getPrice())
			.discountRate(product.getDiscountRate())
			.discountPrice(product.getDiscountPrice())
			.finalPrice(calculateFinalPrice(product))
			.thumbnailImageUrl(getFirstImage(product.getImageUrls()))
			.brandName(product.getBrand().getName())
			.productType(product.getProductType())
			.productTypeDescription(product.getProductType().getDescription())
			.viewCount(product.getViewCount())
			.purchaseCount(product.getPurchaseCount())
			.build();
	}

	// 상품 옵션 응답 변환
	private ProductOptionResponse toOptionResponse(ProductOption option) {
		return ProductOptionResponse.builder()
			.optionId(option.getId())
			.size(option.getSize())
			.color(option.getColor())
			.stock(option.getStock())
			.additionalPrice(option.getAdditionalPrice())
			.available(option.isInStock())
			.lowStock(option.isLowStock())
			.soldOut(option.isSoldOut())
			.build();
	}

	// 상품 리스트 간단 응답 변환
	public List<ProductSimpleResponse> toSimpleResponseList(List<Product> products) {
		return products.stream()
			.map(this::toSimpleResponse)
			.collect(Collectors.toList());
	}

	// 옵션 리스트 변환
	private List<ProductOptionResponse> convertOptions(List<ProductOption> options) {
		if (options == null || options.isEmpty()) {
			return Collections.emptyList();
		}

		return options.stream()
			.map(this::toOptionResponse)
			.collect(Collectors.toList());
	}

	/**
	 * AI 소재 설명 변환
	 * DB에 저장된 텍스트(줄바꿈 구분) → List로 변환
	 */
	private ProductDetailResponse.MaterialDescription convertMaterialDescription(Product product) {
		return ProductDetailResponse.MaterialDescription.builder()
			.advantages(splitByNewLine(product.getAiMaterialAdvantages()))
			.disadvantages(splitByNewLine(product.getAiMaterialDisadvantages()))
			.care(splitByNewLine(product.getAiMaterialCare()))
			.build();
	}

	/**
	 * 줄바꿈(\n)으로 구분된 문자열을 List로 변환
	 * 예: "장점1\n장점2\n장점3" → ["장점1", "장점2", "장점3"]
	 */
	private List<String> splitByNewLine(String text) {
		if (text == null || text.trim().isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.stream(text.split("\n"))
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.collect(Collectors.toList());
	}

	// 최종 가격 계산 (할인 가격이 있으면 할인 가격, 없으면 원래 가격)
	private Integer calculateFinalPrice(Product product) {
		if (product.getDiscountPrice() != null && product.getDiscountPrice() > 0) {
			return product.getDiscountPrice();
		}
		return product.getPrice();
	}

	/**
	 * 쉼표로 구분된 문자열을 List로 변환
	 * 예: "S,M,L,XL" -> ["S", "M", "L", "XL"]
	 */
	private List<String> parseStringToList(String str) {
		if (str == null || str.trim().isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.stream(str.split(","))
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.collect(Collectors.toList());
	}

	// 첫 번째 이미지 URL 가져오기
	private String getFirstImage(String imageUrls) {
		List<String> images = parseStringToList(imageUrls);
		return images.isEmpty() ? null : images.get(0);
	}
}