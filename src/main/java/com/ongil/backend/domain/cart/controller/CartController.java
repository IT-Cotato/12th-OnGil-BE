package com.ongil.backend.domain.cart.controller;

import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ongil.backend.domain.cart.dto.request.CartCreateRequest;
import com.ongil.backend.domain.cart.dto.request.CartUpdateRequest;
import com.ongil.backend.domain.cart.dto.response.CartResponse;
import com.ongil.backend.domain.cart.service.CartService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Cart", description = "장바구니 API")
@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@Operation(summary = "내 장바구니 조회", description = "로그인한 사용자의 장바구니 목록을 조회합니다.")
	@GetMapping
	public DataResponse<List<CartResponse>> getMyCarts(
		@AuthenticationPrincipal Long userId
	) {
		List<CartResponse> carts = cartService.getMyCarts(userId);
		return DataResponse.from(carts);
	}

	@Operation(summary = "장바구니 추가", description = "상품을 장바구니에 추가합니다. 같은 옵션이 있으면 수량만 증가합니다.")
	@PostMapping
	public DataResponse<CartResponse> addCart(
		@AuthenticationPrincipal Long userId,
		@RequestBody @Valid CartCreateRequest request
	) {
		CartResponse response = cartService.addCart(userId, request);
		return DataResponse.from(response);
	}

	@Operation(summary = "수량/옵션 변경", description = "장바구니 항목의 수량 또는 옵션을 변경합니다.")
	@PatchMapping("/{cartId}")
	public DataResponse<CartResponse> updateCart(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long cartId,
		@RequestBody @Valid CartUpdateRequest request
	) {
		CartResponse response = cartService.updateCart(userId, cartId, request);
		return DataResponse.from(response);
	}

	@Operation(summary = "장바구니 개별 삭제", description = "장바구니에서 특정 상품을 삭제합니다.")
	@DeleteMapping("/{cartId}")
	public DataResponse<String> deleteCart(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long cartId
	) {
		cartService.deleteCart(userId, cartId);
		return DataResponse.from("장바구니에서 삭제되었습니다.");
	}

	@Operation(summary = "장바구니 선택 삭제", description = "선택한 여러 상품을 한 번에 삭제합니다.")
	@DeleteMapping
	public DataResponse<String> deleteCarts(
		@AuthenticationPrincipal Long userId,
		@RequestParam List<Long> ids
	) {
		cartService.deleteCarts(userId, ids);
		return DataResponse.from("선택한 상품이 삭제되었습니다.");
	}

	@Operation(summary = "장바구니 담긴 개수 조회 (홈 화면 뱃지용)",
			description = "로그인한 유저의 장바구니에 담긴 상품 종류의 수(Count)를 반환합니다. 0개면 0을 반환합니다.")
	@GetMapping("/count")
	public DataResponse<Map<String, Long>> getCartCount(
			@AuthenticationPrincipal Long userId
	) {
		long count = cartService.getCartCount(userId);

		// JSON 결과: { "count": 3 } 형태
		return DataResponse.from(Map.of("count", count));
	}
}
