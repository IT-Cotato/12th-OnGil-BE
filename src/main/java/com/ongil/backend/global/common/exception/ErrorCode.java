package com.ongil.backend.global.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// 공통 에러
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.", "COMMON-001"),
	INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "요청 파라미터가 잘못되었습니다.", "COMMON-002"),
	NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없습니다.", "COMMON-003"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에서 에러가 발생하였습니다.", "COMMON-004"),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다.", "COMMON-005"),
	FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", "COMMON-006"),

	// AUTH
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.", "AUTH-001"),
	REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 존재하지 않습니다.", "AUTH-002"),
	STOLEN_REFRESH_TOKEN(HttpStatus.FORBIDDEN, "탈취된 토큰으로 의심됩니다. 다시 로그인해주세요.", "AUTH-003"),
	INVALID_SOCIAL_USER_INFO(HttpStatus.BAD_REQUEST, "사용자 정보가 올바르지 않습니다.", "AUTH-004"),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.", "AUTH-005"),

	// USER
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다.", "USER-001"),
	INVALID_SIZE(HttpStatus.BAD_REQUEST, "유효하지 않은 사이즈입니다.", "USER-002"),

	// PRODUCT
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다.", "PRODUCT-001"),

	// BRAND
	BRAND_NOT_FOUND(HttpStatus.NOT_FOUND, "브랜드를 찾을 수 없습니다.", "BRAND-001"),

	// CATEGORY
	CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다.", "CATEGORY-001"),

	// WISHLIST
	WISHLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "찜을 찾을 수 없습니다.", "WISHLIST-001"),
	WISHLIST_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 찜한 상품입니다.", "WISHLIST-002"),
	WISHLIST_FORBIDDEN(HttpStatus.FORBIDDEN, "본인의 찜만 삭제할 수 있습니다.", "WISHLIST-003"),

	// CART
	CART_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니를 찾을 수 없습니다.", "CART-001"),
	CART_FORBIDDEN(HttpStatus.FORBIDDEN, "본인의 장바구니만 수정/삭제할 수 있습니다.", "CART-002"),

	// REVIEW
	REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다.", "REVIEW-001"),
	REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "본인의 리뷰만 수정/삭제할 수 있습니다.", "REVIEW-002"),

	// ORDER
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다.", "ORDER-001"),
	ORDER_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 상품을 찾을 수 없습니다.", "ORDER-002"),

	// ADDRESS
	ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "배송지 정보를 찾을 수 없습니다.", "ADDRESS-001"),
	ADDRESS_FORBIDDEN(HttpStatus.FORBIDDEN, "본인의 배송지만 수정/삭제할 수 있습니다.", "ADDRESS-002"),

	// MAGAZINE
	MAGAZINE_NOT_FOUND(HttpStatus.NOT_FOUND, "매거진을 찾을 수 없습니다.", "MAG-001"),
	COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다.", "COMM-002"),
	;

	private final HttpStatus httpStatus;
	private final String message;
	private final String code;
}