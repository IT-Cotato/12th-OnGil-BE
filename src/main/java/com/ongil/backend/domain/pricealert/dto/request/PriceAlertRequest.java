package com.ongil.backend.domain.pricealert.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "할인 알림 설정 요청")
public class PriceAlertRequest {

	@NotNull(message = "상품 ID는 필수입니다.")
	@Schema(description = "상품 ID", example = "1")
	private Long productId;

	@NotNull(message = "할인율은 필수입니다.")
	@Schema(description = "할인율 (10, 20, 30, 40 중 선택)", example = "20", allowableValues = {"10", "20", "30", "40"})
	private Integer discountRate;
}