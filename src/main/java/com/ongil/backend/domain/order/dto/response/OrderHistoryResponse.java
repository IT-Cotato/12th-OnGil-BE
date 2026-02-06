package com.ongil.backend.domain.order.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 내역 응답")
public record OrderHistoryResponse(
	@Schema(description = "주문 목록")
	List<OrderSummaryDto> content,

	@Schema(description = "전체 주문 수")
	long totalElements,

	@Schema(description = "전체 페이지 수")
	int totalPages,

	@Schema(description = "현재 페이지 번호")
	int currentPage
) {
}
