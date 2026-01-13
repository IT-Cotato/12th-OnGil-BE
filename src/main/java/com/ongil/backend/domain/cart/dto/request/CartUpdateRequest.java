package com.ongil.backend.domain.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

public record CartUpdateRequest(

	@Schema(description = "변경할 사이즈 (선택)", example = "XL")
	String selectedSize,

	@Schema(description = "변경할 색상 (선택)", example = "네이비")
	String selectedColor,

	@Schema(description = "변경할 수량 (선택)", example = "3")
	@Min(value = 1, message = "수량은 최소 1개 이상이어야 합니다")
	Integer quantity
) {
}
