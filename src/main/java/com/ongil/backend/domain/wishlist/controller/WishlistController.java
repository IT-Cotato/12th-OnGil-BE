package com.ongil.backend.domain.wishlist.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ongil.backend.domain.wishlist.dto.response.WishlistResponse;
import com.ongil.backend.domain.wishlist.service.WishlistService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Wishlist", description = "찜 API")
@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
public class WishlistController {

	private final WishlistService wishlistService;

	@Operation(summary = "상품 찜하기", description = "특정 상품을 찜 목록에 추가합니다.(토큰 필요)")
	@PostMapping("/products/{productId}")
	public DataResponse<WishlistResponse> addWishlist(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long productId
	) {
		WishlistResponse response = wishlistService.addWishlist(userId, productId);
		return DataResponse.from(response);
	}

	@Operation(summary = "찜 취소", description = "찜 목록에서 특정 상품을 삭제합니다.(토큰 필요)")
	@DeleteMapping("/{wishlistId}")
	public DataResponse<String> removeWishlist(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long wishlistId
	) {
		wishlistService.removeWishlist(userId, wishlistId);
		return DataResponse.from("찜 목록에서 삭제되었습니다.");
	}

	@Operation(summary = "내 찜 목록 조회", description = "로그인한 사용자의 찜 목록을 조회합니다. 카테고리별 필터링 가능합니다.(토큰 필요)")
	@GetMapping
	public DataResponse<List<WishlistResponse>> getMyWishlists(
		@AuthenticationPrincipal Long userId,
		@RequestParam(required = false) Long categoryId
	) {
		List<WishlistResponse> wishlists = wishlistService.getMyWishlists(userId, categoryId);
		return DataResponse.from(wishlists);
	}
}