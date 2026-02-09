package com.ongil.backend.domain.order.dto.request;

import com.ongil.backend.domain.order.enums.CancelReason;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "환불 정보 조회 요청")
public record CancelRefundInfoRequest(

	@Schema(description = "취소 사유")
	@NotNull(message = "취소 사유는 필수입니다")
	CancelReason cancelReason,

	@Schema(description = "상세 취소 사유 (1~300자)")
	@Size(min = 1, max = 300, message = "상세 사유는 1자 이상 300자 이하로 입력해주세요")
	String cancelDetail
) {
}
