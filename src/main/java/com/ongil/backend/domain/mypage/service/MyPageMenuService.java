package com.ongil.backend.domain.mypage.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.cart.repository.CartRepository;
import com.ongil.backend.domain.mypage.converter.MyPageMenuConverter;
import com.ongil.backend.domain.mypage.dto.response.MyPageMenuItemResponse;
import com.ongil.backend.domain.mypage.dto.response.MyPageMenuResponse;
import com.ongil.backend.domain.mypage.enums.MyPageMenuType;
import com.ongil.backend.domain.review.service.ReviewService;
import com.ongil.backend.domain.wishlist.repository.WishlistRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageMenuService {

	private final CartRepository cartRepository;
	private final WishlistRepository wishlistRepository;
	private final ReviewService reviewService;
	private final MyPageMenuConverter myPageMenuConverter;

	public MyPageMenuResponse getMyPageMenu(Long userId) {
		List<MyPageMenuItemResponse> menuItems = new ArrayList<>();

		// 각 메뉴 아이템에 배지 카운트 계산
		menuItems.add(myPageMenuConverter.toMenuItemResponse(MyPageMenuType.PROFILE, null));
		menuItems.add(myPageMenuConverter.toMenuItemResponse(MyPageMenuType.ORDERS, null));
		
		// 작성 가능한 리뷰 개수 계산 (작성 대기 중인 주문 아이템)
		Integer pendingReviewCount = getPendingReviewCount(userId);
		menuItems.add(myPageMenuConverter.toMenuItemResponse(MyPageMenuType.REVIEWS, pendingReviewCount));
		
		// 찜 목록 개수
		Integer wishlistCount = getWishlistCount(userId);
		menuItems.add(myPageMenuConverter.toMenuItemResponse(MyPageMenuType.WISHLIST, wishlistCount));
		
		// 장바구니 개수
		Integer cartCount = getCartCount(userId);
		menuItems.add(myPageMenuConverter.toMenuItemResponse(MyPageMenuType.CART, cartCount));
		
		menuItems.add(myPageMenuConverter.toMenuItemResponse(MyPageMenuType.ADDRESSES, null));
		menuItems.add(myPageMenuConverter.toMenuItemResponse(MyPageMenuType.POINTS, null));
		menuItems.add(myPageMenuConverter.toMenuItemResponse(MyPageMenuType.SETTINGS, null));

		return myPageMenuConverter.toMenuResponse(menuItems);
	}

	private Integer getCartCount(Long userId) {
		long count = cartRepository.countByUserId(userId);
		return count > 0 ? (int) count : null;
	}

	private Integer getWishlistCount(Long userId) {
		long count = wishlistRepository.countByUserId(userId);
		return count > 0 ? (int) count : null;
	}

	private Integer getPendingReviewCount(Long userId) {
		// 주문 완료 후 리뷰 미작성한 주문 아이템 개수
		int count = reviewService.getPendingReviewCount(userId);
		return count > 0 ? count : null;
	}
}
