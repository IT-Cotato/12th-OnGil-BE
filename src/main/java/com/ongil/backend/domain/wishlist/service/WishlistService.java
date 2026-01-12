package com.ongil.backend.domain.wishlist.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.domain.wishlist.converter.WishlistConverter;
import com.ongil.backend.domain.wishlist.dto.response.WishlistResponse;
import com.ongil.backend.domain.wishlist.entity.Wishlist;
import com.ongil.backend.domain.wishlist.repository.WishlistRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.common.exception.ValidationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistService {

	private final WishlistRepository wishlistRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final WishlistConverter wishlistConverter;

	// 상품 찜하기
	@Transactional
	public WishlistResponse addWishlist(Long userId, Long productId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

		if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
			throw new ValidationException(ErrorCode.WISHLIST_ALREADY_EXISTS);
		}

		Wishlist wishlist = Wishlist.builder()
			.user(user)
			.product(product)
			.build();

		Wishlist savedWishlist = wishlistRepository.save(wishlist);

		return wishlistConverter.toResponse(savedWishlist);
	}

	// 찜 삭제
	@Transactional
	public void removeWishlist(Long userId, Long wishlistId) {
		Wishlist wishlist = wishlistRepository.findById(wishlistId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.WISHLIST_NOT_FOUND));

		if (!wishlist.getUser().getId().equals(userId)) {
			throw new ValidationException(ErrorCode.WISHLIST_FORBIDDEN);
		}

		wishlistRepository.delete(wishlist);
	}

	// 내 찜 목록 조회 (카테고리 필터링 가능)
	public List<WishlistResponse> getMyWishlists(Long userId, Long categoryId) {
		List<Wishlist> wishlists;

		if (categoryId != null) {
			// 카테고리 필터링
			wishlists = wishlistRepository.findByUserIdAndCategoryWithProduct(userId, categoryId);
		} else {
			// 전체 조회
			wishlists = wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId);
		}

		return wishlistConverter.toResponseList(wishlists);
	}
}