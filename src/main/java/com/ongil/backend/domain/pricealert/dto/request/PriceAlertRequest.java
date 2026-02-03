package com.ongil.backend.domain.pricealert.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
@Schema(description = "할인 알림 설정 요청")
public class PriceAlertRequest {

	@NotNull(message = "상품 ID는 필수입니다.")
	@Schema(description = "상품 ID", example = "1")
	private Long productId;

	@NotNull(message = "목표 가격은 필수입니다.")
	@Positive(message = "목표 가격은 양수여야 합니다.")
	@Schema(description = "목표 가격 (사용자가 선택한 할인가)", example = "47500")
	private Integer targetPrice;
}