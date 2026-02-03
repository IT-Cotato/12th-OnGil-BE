package com.ongil.backend.domain.pricealert.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "할인 알림 조회 응답")
public class PriceAlertResponse {

	@Schema(description = "상품 ID", example = "1")
	private Long productId;

	@Schema(description = "현재 판매가", example = "50000")
	private Integer currentPrice;

	@Schema(description = "사용자가 선택한 목표 가격", example = "47500")
	private Integer targetPrice;

	@Schema(description = "알림 발송 여부", example = "false")
	private Boolean isNotified;

	@Schema(description = "알림 설정 활성 여부", example = "true")
	private Boolean isActive;
}