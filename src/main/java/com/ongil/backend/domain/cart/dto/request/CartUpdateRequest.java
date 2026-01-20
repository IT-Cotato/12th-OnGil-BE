package com.ongil.backend.domain.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(description = "장바구니 수정 요청")
public record CartUpdateRequest(

	@Schema(description = "변경할 사이즈 (선택)")
	String selectedSize,

	@Schema(description = "변경할 색상 (선택)")
	String selectedColor,

	@Schema(description = "변경할 수량 (선택)")
	@Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다")
	Integer quantity
) {
}