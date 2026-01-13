package com.ongil.backend.domain.cart.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.cart.dto.response.CartResponse;
import com.ongil.backend.domain.cart.entity.Cart;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.common.exception.ValidationException;

@Component
public class CartConverter {

	public CartResponse toResponse(Cart cart) {
		Product product = cart.getProduct();
		if (product == null) {
			throw new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
		}

		if (product.getBrand() == null) {
			throw new ValidationException(ErrorCode.INVALID_PARAMETER);
		}

		// 개당 가격 계산
		Integer price = calculatePrice(product);

		return CartResponse.builder()
			.cartId(cart.getId())
			.productId(product.getId())
			.productName(product.getName())
			.brandName(product.getBrand().getName())
			.thumbnailImageUrl(getFirstImage(product.getImageUrls()))
			.selectedSize(cart.getSelectedSize())
			.selectedColor(cart.getSelectedColor())
			.quantity(cart.getQuantity())
			.price(price)
			.totalPrice(price * cart.getQuantity())
			.build();
	}

	public List<CartResponse> toResponseList(List<Cart> carts) {
		return carts.stream()
			.map(this::toResponse)
			.collect(Collectors.toList());
	}

	// 개당 가격 계산 (할인가 우선)
	private Integer calculatePrice(Product product) {
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
}
