package com.ongil.backend.domain.product.converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.category.entity.Category;
import com.ongil.backend.domain.category.enums.SizeChartType;
import com.ongil.backend.domain.product.dto.response.ProductDetailResponse;
import com.ongil.backend.domain.product.dto.response.ProductOptionResponse;
import com.ongil.backend.domain.product.dto.response.ProductSimpleResponse;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.entity.ProductOption;

@Component
public class ProductConverter {

	// 상품 상세 응답 변환
	public ProductDetailResponse toDetailResponse(Product product, List<ProductOption> options) {

		if (product.getBrand() == null || product.getCategory() == null) {
			throw new IllegalStateException("상품의 브랜드 또는 카테고리 정보가 누락되었습니다.");
		}

		return ProductDetailResponse.builder()
			.id(product.getId())
			.name(product.getName())
			.description(product.getDescription())
			.price(product.getPrice())
			.discountRate(product.getDiscountRate())
			.finalPrice(calculateFinalPrice(product))
			.materialOriginal(product.getMaterialOriginal())
			.materialDescription(convertMaterialDescription(product))
			.options(convertOptions(options))
			.imageUrls(parseStringToList(product.getImageUrls()))
			.brandId(product.getBrand().getId())
			.brandName(product.getBrand().getName())
			.categoryId(product.getCategory().getId())
			.categoryName(product.getCategory().getName())
			.sizeChartType(getSizeChartType(product))
			.onSale(product.getOnSale())
			.productType(product.getProductType())
			.reviewCount(product.getReviewCount())
			.reviewRating(product.getReviewRating())
			.build();
	}

	// 상품 간단 응답 변환
	public ProductSimpleResponse toSimpleResponse(Product product) {

		if (product.getBrand() == null) {
			throw new IllegalStateException("상품의 브랜드 정보가 누락되었습니다.");
		}

		return ProductSimpleResponse.builder()
			.id(product.getId())
			.name(product.getName())
			.price(product.getPrice())
			.discountRate(product.getDiscountRate())
			.finalPrice(calculateFinalPrice(product))
			.thumbnailImageUrl(getFirstImage(product.getImageUrls()))
			.brandId(product.getBrand().getId())
			.brandName(product.getBrand().getName())
			.productType(product.getProductType())
			.viewCount(product.getViewCount())
			.purchaseCount(product.getPurchaseCount())
			.reviewCount(product.getReviewCount())
			.reviewRating(product.getReviewRating())
			.build();
	}

	// 상품 옵션 응답 변환
	private ProductOptionResponse toOptionResponse(ProductOption option) {
		return ProductOptionResponse.builder()
			.optionId(option.getId())
			.size(option.getSize())
			.color(option.getColor())
			.stock(option.getStock())
			.stockStatus(option.getStockStatus())
			.build();
	}

	// 상품 리스트 간단 응답 변환
	public List<ProductSimpleResponse> toSimpleResponseList(List<Product> products) {
		return products.stream()
			.map(this::toSimpleResponse)
			.collect(Collectors.toList());
	}

	// 옵션 리스트 변환
	public List<ProductOptionResponse> convertOptions(List<ProductOption> options) {
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

	// 사이즈표 타입 가져오기
	private SizeChartType getSizeChartType(Product product) {
		Category category = product.getCategory();

		// 하위 카테고리라면 상위 카테고리의 타입 사용
		if (category.getParentCategory() != null) {
			return category.getParentCategory().getSizeChartType();
		}

		// 상위 카테고리라면 자기 자신의 타입 사용
		return category.getSizeChartType();
	}
}