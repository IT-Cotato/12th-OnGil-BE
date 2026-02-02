package com.ongil.backend.domain.cart.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.cart.converter.CartConverter;
import com.ongil.backend.domain.cart.dto.request.CartCreateRequest;
import com.ongil.backend.domain.cart.dto.request.CartUpdateRequest;
import com.ongil.backend.domain.cart.dto.response.CartResponse;
import com.ongil.backend.domain.cart.entity.Cart;
import com.ongil.backend.domain.cart.repository.CartRepository;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.common.exception.ValidationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

	private final CartRepository cartRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final CartConverter cartConverter;

	// 홈 화면 뱃지용 카운트 조회
	public long getCartCount(Long userId) {
		return cartRepository.countByUserId(userId);
	}

	// 내 장바구니 조회
	public List<CartResponse> getMyCarts(Long userId) {
		List<Cart> carts = cartRepository.findByUserIdOrderByCreatedAtDesc(userId);
		return cartConverter.toResponseList(carts);
	}

	// 장바구니 추가
	@Transactional
	public CartResponse addCart(Long userId, CartCreateRequest request) {

		if (!userRepository.existsById(userId)) {
			throw new EntityNotFoundException(ErrorCode.USER_NOT_FOUND);
		}

		Product product = productRepository.findWithBrandAndCategoryById(request.productId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

		Optional<Cart> existingCart = cartRepository
			.findByUserIdAndProductIdAndSelectedSizeAndSelectedColor(
				userId,
				request.productId(),
				request.selectedSize(),
				request.selectedColor()
			);

		if (existingCart.isPresent()) {
			Cart cart = existingCart.get();
			cart.updateQuantity(cart.getQuantity() + request.quantity());
			return cartConverter.toResponse(cart);
		}

		Cart cart = Cart.builder()
			.user(User.builder().id(userId).build())
			.product(product)
			.selectedSize(request.selectedSize())
			.selectedColor(request.selectedColor())
			.quantity(request.quantity())
			.build();

		Cart savedCart = cartRepository.save(cart);

		// 상품의 장바구니 담긴 횟수 증가
		product.incrementCartCount();

		return cartConverter.toResponse(savedCart);
	}

	// 장바구니 수정 (수량/옵션 변경)
	@Transactional
	public CartResponse updateCart(Long userId, Long cartId, CartUpdateRequest request) {

		Cart cart = cartRepository.findByIdAndUserId(cartId, userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.CART_NOT_FOUND));

		// 옵션 업데이트
		if (request.selectedSize() != null) {
			cart.updateSize(request.selectedSize());
		}

		if (request.selectedColor() != null) {
			cart.updateColor(request.selectedColor());
		}

		if (request.quantity() != null) {
			cart.updateQuantity(request.quantity());
		}

		return cartConverter.toResponse(cart);
	}

	// 장바구니 개별 삭제
	@Transactional
	public void deleteCart(Long userId, Long cartId) {
		int deleted = cartRepository.deleteByIdAndUserId(cartId, userId);

		if (deleted == 0) {
			throw new EntityNotFoundException(ErrorCode.CART_NOT_FOUND);
		}
	}

	// 장바구니 선택 삭제
	@Transactional
	public void deleteCarts(Long userId, List<Long> cartIds) {
		if (cartIds == null || cartIds.isEmpty()) {
			throw new ValidationException(ErrorCode.INVALID_PARAMETER);
		}

		List<Long> distinctIds = cartIds.stream()
			.distinct()
			.collect(Collectors.toList());

		int deleted = cartRepository.deleteByIdInAndUserId(distinctIds, userId);
		
		if (deleted != distinctIds.size()) {
			throw new EntityNotFoundException(ErrorCode.CART_NOT_FOUND);
		}
	}
}