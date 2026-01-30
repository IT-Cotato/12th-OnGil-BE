package com.ongil.backend.domain.order.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.order.dto.request.CartOrderRequest;
import com.ongil.backend.domain.order.dto.request.OrderCreateRequest;
import com.ongil.backend.domain.order.dto.response.OrderDetailResponse;
import com.ongil.backend.domain.order.service.OrderService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "결제 및 주문 관련 API")
public class OrderController {

	private final OrderService orderService;

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
