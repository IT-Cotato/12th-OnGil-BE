package com.ongil.backend.domain.order.dto.response;

import java.time.LocalDateTime;

import com.ongil.backend.domain.order.enums.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 목록 응답")
public record OrderListResponse(
	@Schema(description = "주문 ID")
	Long orderId,
	
	@Schema(description = "주문 번호")
	String orderNumber,
	
	@Schema(description = "주문 상태")
	OrderStatus orderStatus,
	
	@Schema(description = "대표 상품명 (첫 번째 상품)")
	String representativeProductName,
	
	@Schema(description = "대표 상품 이미지 URL")
	String representativeImageUrl,
	
	@Schema(description = "주문 상품 수량")
	Integer totalItemCount,
	
	@Schema(description = "총 결제 금액")
	Integer totalAmount,
	
	@Schema(description = "주문 생성일")
	LocalDateTime createdAt
) {}
