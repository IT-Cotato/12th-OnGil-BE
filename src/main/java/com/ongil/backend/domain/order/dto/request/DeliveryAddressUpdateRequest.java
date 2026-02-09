package com.ongil.backend.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "주문 배송지 변경 요청")
public record DeliveryAddressUpdateRequest(

	@Schema(description = "변경할 배송지 ID")
	@NotNull(message = "배송지 ID는 필수입니다")
	Long addressId
) {
}
