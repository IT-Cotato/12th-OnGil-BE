package com.ongil.backend.wishlist.dto.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.category.entity.Category;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.wishlist.entity.Wishlist;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.common.exception.ValidationException;
import com.ongil.backend.wishlist.dto.response.WishlistResponse;

@Component
public class WishlistConverter {

	public WishlistResponse toResponse(Wishlist wishlist) {
		Product product = wishlist.getProduct();
		if (product == null) {
			throw new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
		}

		if (product.getBrand() == null || product.getCategory() == null) {
			throw new ValidationException(ErrorCode.INVALID_PARAMETER);
		}

		return WishlistResponse.builder()
			.wishlistId(wishlist.getId())
			.productId(product.getId())
			.productName(product.getName())
			.brandName(product.getBrand().getName())
			.originalPrice(product.getPrice())
			.discountRate(product.getDiscountRate())
			.discountPrice(product.getDiscountPrice())
			.finalPrice(calculateFinalPrice(product))
			.thumbnailImageUrl(getFirstImage(product.getImageUrls()))
			.categoryId(getParentCategoryId(product.getCategory()))
			.categoryName(getParentCategoryName(product.getCategory()))
			.build();
	}

	public List<WishlistResponse> toResponseList(List<Wishlist> wishlists) {
		return wishlists.stream()
			.map(this::toResponse)
			.collect(Collectors.toList());
	}

	// 최종 가격 계산
	private Integer calculateFinalPrice(Product product) {
		if (product.getDiscountPrice() != null && product.getDiscountPrice() > 0) {
			return product.getDiscountPrice();
		}
		return product.getPrice();
	}

	// 첫 번째 이미지 URL 가져오기
	private String getFirstImage(String imageUrls) {
		if (imageUrls == null || imageUrls.trim().isEmpty()) {
			return null;
		}
		String[] images = imageUrls.split(",");
		return images.length > 0 ? images[0].trim() : null;
	}

	// 상위 카테고리 ID 가져오기
	private Long getParentCategoryId(Category category) {
		if (category.getParentCategory() != null) {
			return category.getParentCategory().getId();
		}
		return category.getId(); // 자신이 상위 카테고리
	}

	// 상위 카테고리 이름 가져오기
	private String getParentCategoryName(Category category) {
		if (category.getParentCategory() != null) {
			return category.getParentCategory().getName();
		}
		return category.getName(); // 자신이 상위 카테고리
	}
}