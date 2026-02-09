package com.ongil.backend.domain.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "환불 정보")
public record RefundInfoDto(

	@Schema(description = "상품 금액")
	Integer productAmount,

	@Schema(description = "배송비")
	Integer shippingFee,

	@Schema(description = "환불 금액 (상품 금액 - 배송비)")
	Integer refundAmount
) {
}
