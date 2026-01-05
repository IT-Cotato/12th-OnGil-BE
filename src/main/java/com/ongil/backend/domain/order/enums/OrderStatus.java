package com.ongil.backend.domain.order.enums;

public enum OrderStatus {
	ORDER_RECEIVED,  // 주문 접수
	SHIPPING,        // 배송 중
	DELIVERED,       // 배송 완료
	CONFIRMED,       // 구매 확정
	CANCELED         // 취소
}
