package com.ongil.backend.domain.cart.service;

import java.util.List;
import java.util.Optional;

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
	public List<CartResponse> getMyCarts(Long userId) {
		List<Cart> carts = cartRepository.findByUserIdOrderByCreatedAtDesc(userId);
		return cartConverter.toResponseList(carts);
	}

	@Transactional
	public CartResponse addCart(Long userId, CartCreateRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

		Product product = productRepository.findById(request.productId())
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
			.user(user)
			.product(product)
			.selectedSize(request.selectedSize())
			.selectedColor(request.selectedColor())
			.quantity(request.quantity())
			.build();

		Cart savedCart = cartRepository.save(cart);
		return cartConverter.toResponse(savedCart);
	}

	@Transactional
	public CartResponse updateCart(Long userId, Long cartId, CartUpdateRequest request) {
		Cart cart = cartRepository.findById(cartId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.CART_NOT_FOUND));

		if (!cart.getUser().getId().equals(userId)) {
			throw new ValidationException(ErrorCode.CART_FORBIDDEN);
		}

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

	@Transactional
	public void deleteCart(Long userId, Long cartId) {
		Cart cart = cartRepository.findById(cartId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.CART_NOT_FOUND));

		if (!cart.getUser().getId().equals(userId)) {
			throw new ValidationException(ErrorCode.CART_FORBIDDEN);
		}

		cartRepository.delete(cart);
	}

	@Transactional
	public void deleteCarts(Long userId, List<Long> cartIds) {

		if (cartIds == null || cartIds.isEmpty()) {
			throw new ValidationException(ErrorCode.INVALID_PARAMETER);
		}

		List<Cart> carts = cartRepository.findAllById(cartIds);

		if (carts.size() != cartIds.size()) {
			throw new EntityNotFoundException(ErrorCode.CART_NOT_FOUND);
		}

		carts.forEach(cart -> {
			if (!cart.getUser().getId().equals(userId)) {
				throw new ValidationException(ErrorCode.CART_FORBIDDEN);
			}
		});

		cartRepository.deleteAll(carts);
	}
}
