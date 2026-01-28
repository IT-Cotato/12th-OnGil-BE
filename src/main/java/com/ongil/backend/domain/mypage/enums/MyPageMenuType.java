package com.ongil.backend.domain.mypage.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MyPageMenuType {
	PROFILE("내 정보", "/api/users/me", "프로필 및 개인정보 관리"),
	ORDERS("주문 내역", "/api/users/me/orders", "주문 및 배송 조회"),
	REVIEWS("내 리뷰", "/api/users/me/reviews", "작성한 리뷰 및 작성 가능한 리뷰"),
	WISHLIST("찜 목록", "/api/wishlists", "관심 상품 관리"),
	CART("장바구니", "/api/carts", "장바구니 상품 관리"),
	ADDRESSES("배송지 관리", "/api/users/me/addresses", "배송지 등록 및 수정"),
	POINTS("포인트", "/api/users/me/points", "포인트 조회 및 사용 내역"),
	SETTINGS("설정", "/api/users/me/settings", "알림 및 개인 설정");

	private final String displayName;
	private final String apiPath;
	private final String description;
}
