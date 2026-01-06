package com.ongil.backend.domain.payment.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
	CARD("카드 결제"),
	BANK_TRANSFER("계좌 이체"),
	VIRTUAL_ACCOUNT("가상 계좌");

	private final String description;
}
