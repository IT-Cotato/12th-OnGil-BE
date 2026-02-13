package com.ongil.backend.domain.order.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
	CONFIRMED("구매 확정"),
	CANCELED("취소");
	private final String description;
}
