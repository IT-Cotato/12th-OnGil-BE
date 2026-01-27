package com.ongil.backend.domain.review.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ongil.backend.domain.review.dto.request.ReviewListRequest;
import com.ongil.backend.domain.review.dto.response.*;
import com.ongil.backend.domain.review.enums.ReviewType;
import com.ongil.backend.domain.review.service.ReviewService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Review", description = "리뷰 API")
@RestController
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@Operation(summary = "상품별 리뷰 목록 조회", description = "상품의 리뷰 목록을 조회합니다. 유사 체형 필터, 사이즈/색상 필터, 정렬을 지원합니다.")
	@GetMapping("/api/products/{productId}/reviews")
	public DataResponse<Page<ReviewListResponse>> getProductReviews(
		@PathVariable Long productId,
		@AuthenticationPrincipal Long userId,
		@ModelAttribute ReviewListRequest request
	) {
		Page<ReviewListResponse> reviews = reviewService.getProductReviews(productId, userId, request);
		return DataResponse.from(reviews);
	}

	@Operation(summary = "리뷰 통계 요약 조회", description = "상품의 리뷰 통계 요약을 조회합니다. 사이즈는 유사 체형 기준, 색감/소재는 전체 유저 기준입니다.")
	@GetMapping("/api/products/{productId}/reviews/summary")
	public DataResponse<ReviewSummaryResponse> getReviewSummary(
		@PathVariable Long productId,
		@AuthenticationPrincipal Long userId
	) {
		ReviewSummaryResponse summary = reviewService.getReviewSummary(productId, userId);
		return DataResponse.from(summary);
	}

	@Operation(summary = "리뷰 상세 조회", description = "리뷰의 상세 정보를 조회합니다. 모든 선택지 답변을 부문별로 반환합니다.")
	@GetMapping("/api/reviews/{reviewId}/details")
	public DataResponse<ReviewDetailResponse> getReviewDetail(
		@PathVariable Long reviewId,
		@AuthenticationPrincipal Long userId
	) {
		ReviewDetailResponse detail = reviewService.getReviewDetail(reviewId, userId);
		return DataResponse.from(detail);
	}

	@Operation(summary = "내가 작성한 리뷰 조회", description = "로그인한 사용자가 작성한 리뷰 목록을 조회합니다.")
	@GetMapping("/api/users/me/reviews")
	public DataResponse<Page<MyReviewResponse>> getMyReviews(
		@AuthenticationPrincipal Long userId,
		@Parameter(description = "리뷰 타입 필터") @RequestParam(required = false) ReviewType reviewType,
		@Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
		@Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int pageSize
	) {
		Page<MyReviewResponse> reviews = reviewService.getMyReviews(userId, reviewType, page, pageSize);
		return DataResponse.from(reviews);
	}

	@Operation(summary = "작성 가능한 리뷰 목록 조회", description = "로그인한 사용자가 작성 가능한 리뷰 목록을 조회합니다.")
	@GetMapping("/api/users/me/reviews/pending")
	public DataResponse<List<PendingReviewResponse>> getPendingReviews(
		@AuthenticationPrincipal Long userId
	) {
		List<PendingReviewResponse> pendingReviews = reviewService.getPendingReviews(userId);
		return DataResponse.from(pendingReviews);
	}

	@Operation(summary = "리뷰 도움돼요 토글", description = "리뷰의 도움돼요를 토글합니다. 이미 눌렀으면 취소, 안 눌렀으면 추가됩니다.")
	@PostMapping("/api/reviews/{reviewId}/helpful")
	public DataResponse<ReviewHelpfulResponse> toggleHelpful(
		@PathVariable Long reviewId,
		@AuthenticationPrincipal Long userId
	) {
		ReviewHelpfulResponse response = reviewService.toggleHelpful(reviewId, userId);
		return DataResponse.from(response);
	}
}
