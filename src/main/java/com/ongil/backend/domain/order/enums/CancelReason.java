package com.ongil.backend.domain.order.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CancelReason {
	WRONG_ADDRESS("배송지 잘못 입력했어요"),
	SELLER_RESPONSIBILITY("판매자 책임"),
	BUYER_RESPONSIBILITY("구매자 책임");

	private final String description;
}
