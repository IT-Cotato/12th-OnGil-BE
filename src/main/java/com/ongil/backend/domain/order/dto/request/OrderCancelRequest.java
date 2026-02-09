package com.ongil.backend.domain.order.dto.request;

import com.ongil.backend.domain.order.enums.CancelReason;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "주문 취소 요청")
public record OrderCancelRequest(

	@Schema(description = "취소 사유")
	@NotNull(message = "취소 사유는 필수입니다")
	CancelReason cancelReason,

	@Schema(description = "상세 취소 사유 (1~300자)")
	@Size(min = 1, max = 300, message = "상세 사유는 1자 이상 300자 이하로 입력해주세요")
	String cancelDetail,

	@Schema(description = "취소 상품 장바구니에 다시 담기 여부")
	@NotNull(message = "장바구니 담기 여부는 필수입니다")
	Boolean addToCart
) {
}
