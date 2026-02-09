package com.ongil.backend.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "주문 취소 요청")
public record OrderCancelRequest(
	@Schema(description = "취소 사유", example = "단순 변심")
	@NotBlank(message = "취소 사유는 필수입니다.")
	String cancelReason
) {}
