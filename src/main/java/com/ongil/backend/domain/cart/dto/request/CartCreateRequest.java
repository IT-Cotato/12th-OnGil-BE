package com.ongil.backend.domain.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "장바구니 추가 요청")
public record CartCreateRequest(

	@Schema(description = "상품 ID")
	@NotNull(message = "상품 ID는 필수입니다")
	Long productId,

	@Schema(description = "선택한 사이즈")
	String selectedSize,

	@Schema(description = "선택한 색상")
	String selectedColor,

	@Schema(description = "수량")
	@NotNull(message = "수량은 필수입니다")
	@Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다")
	Integer quantity
) {
}