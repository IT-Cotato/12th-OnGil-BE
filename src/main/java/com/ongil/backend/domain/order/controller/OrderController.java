package com.ongil.backend.domain.order.controller;

import java.time.LocalDate;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.order.dto.request.CartOrderRequest;
import com.ongil.backend.domain.order.dto.request.OrderCreateRequest;
import com.ongil.backend.domain.order.dto.response.OrderDetailResponse;
import com.ongil.backend.domain.order.dto.response.OrderHistoryResponse;
import com.ongil.backend.domain.order.service.OrderService;
import com.ongil.backend.global.common.dto.DataResponse;
import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.ErrorCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "결제 및 주문 관련 API")
public class OrderController {

	private final OrderService orderService;

	private static final int MAX_PAGE_SIZE = 100;

	@GetMapping
	@Operation(
		summary = "주문 내역 조회",
		description = "기간별, 키워드별 주문 내역을 조회합니다. " +
			"startDate, endDate를 지정하지 않으면 기본적으로 최근 1년간의 주문을 조회합니다. " +
			"토큰 필요"
	)
	public DataResponse<OrderHistoryResponse> getOrderHistory(
		@AuthenticationPrincipal Long userId,
		@Parameter(description = "검색어 (상품명 또는 주문번호)")
		@RequestParam(required = false) String keyword,
		@Parameter(description = "조회 시작일 (yyyy-MM-dd), 기본값: 1년 전")
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
		@Parameter(description = "조회 종료일 (yyyy-MM-dd), 기본값: 오늘")
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
		@Parameter(description = "페이지 번호 (0부터 시작)")
		@RequestParam(defaultValue = "0") int page,
		@Parameter(description = "페이지 크기 (최대 100)")
		@RequestParam(defaultValue = "10") int size
	) {
		if (page < 0) {
			throw new AppException(ErrorCode.INVALID_PARAMETER, "page는 0 이상이어야 합니다.");
		}
		if (size < 1 || size > MAX_PAGE_SIZE) {
			throw new AppException(ErrorCode.INVALID_PARAMETER, "size는 1 이상 " + MAX_PAGE_SIZE + " 이하여야 합니다.");
		}
		if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
			throw new AppException(ErrorCode.INVALID_PARAMETER, "시작일이 종료일보다 이후일 수 없습니다.");
		}

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		return DataResponse.from(orderService.getOrderHistory(userId, keyword, startDate, endDate, pageable));
	}

	@PostMapping
	@Operation(
		summary = "상품 화면에서 바로 상품 주문",
		description = "장바구니를 거치지 않고 개별 상품을 즉시 주문합니다. 성공 시 생성된 주문 ID(orderId)를 반환합니다."
	)
	public DataResponse<Long> createOrder(
		@AuthenticationPrincipal Long userId, @RequestBody @Valid OrderCreateRequest request
	) {
		Long orderId = orderService.processPayment(userId, request);
		return DataResponse.from(orderId);
	}

	@PostMapping("/cart")
	@Operation(
		summary = "장바구니 상품 주문",
		description = "장바구니에서 선택한 하나 이상의 상품들을 한 번에 주문합니다. " +
			"cartItemIds 필드에 장바구니 항목 ID 리스트(예: [3, 4])를 담아 보냅니다. " +
			"성공 시 생성된 주문 ID(orderId)를 반환하며, 해당 장바구니 항목들은 삭제됩니다."
	)
	public DataResponse<Long> createOrderFromCart(
		@AuthenticationPrincipal Long userId, @RequestBody @Valid CartOrderRequest request
	) {
		Long orderId = orderService.createOrderFromCart(userId, request);
		return DataResponse.from(orderId);
	}

	@GetMapping("/{orderId}")
	@Operation(summary = "주문 상세 조회", description = "orderId를 통한 주문 상세 조회")
	public DataResponse<OrderDetailResponse> getOrderDetail(
		@AuthenticationPrincipal Long userId, @PathVariable Long orderId
	) {
		return DataResponse.from(orderService.getOrderDetail(userId, orderId));
	}

}
