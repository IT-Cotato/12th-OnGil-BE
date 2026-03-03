package com.ongil.backend.domain.review.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ongil.backend.domain.review.dto.request.ReviewFinalSubmitRequest;
import com.ongil.backend.domain.review.dto.request.ReviewListRequest;
import com.ongil.backend.domain.review.dto.request.ReviewStep1Request;
import com.ongil.backend.domain.review.dto.request.ReviewStep2MaterialRequest;
import com.ongil.backend.domain.review.dto.request.ReviewStep2SizeRequest;
import com.ongil.backend.domain.review.dto.response.*;
import com.ongil.backend.domain.review.enums.ReviewType;
import com.ongil.backend.domain.review.service.ReviewCommandService;
import com.ongil.backend.domain.review.service.ReviewQueryService;
import com.ongil.backend.global.common.dto.DataResponse;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.common.exception.ValidationException;
import com.ongil.backend.global.config.s3.S3ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Review", description = "리뷰 API (토큰 필요)")
@RestController
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewCommandService reviewCommandService;
	private final ReviewQueryService reviewQueryService;
	private final S3ImageService s3ImageService;

	@Operation(summary = "상품별 리뷰 목록 조회", description = "상품의 리뷰 목록을 조회합니다. 유사 체형 필터, 사이즈/색상 필터, 정렬을 지원합니다.")
	@GetMapping("/api/products/{productId}/reviews")
	public DataResponse<Page<ReviewListResponse>> getProductReviews(
		@PathVariable Long productId,
		@AuthenticationPrincipal Long userId,
		@ModelAttribute ReviewListRequest request
	) {
		Page<ReviewListResponse> reviews = reviewQueryService.getProductReviews(productId, userId, request);
		return DataResponse.from(reviews);
	}

	@Operation(summary = "리뷰 통계 요약 조회", description = "상품의 리뷰 통계 요약을 조회합니다. 사이즈는 유사 체형 기준, 색감/소재는 전체 유저 기준입니다.")
	@GetMapping("/api/products/{productId}/reviews/summary")
	public DataResponse<ReviewSummaryResponse> getReviewSummary(
		@PathVariable Long productId,
		@AuthenticationPrincipal Long userId
	) {
		ReviewSummaryResponse summary = reviewQueryService.getReviewSummary(productId, userId);
		return DataResponse.from(summary);
	}

	@Operation(summary = "리뷰 상세 조회", description = "리뷰의 상세 정보를 조회합니다. 모든 선택지 답변을 부문별로 반환합니다.")
	@GetMapping("/api/reviews/{reviewId}/details")
	public DataResponse<ReviewDetailResponse> getReviewDetail(
		@PathVariable Long reviewId,
		@AuthenticationPrincipal Long userId
	) {
		ReviewDetailResponse detail = reviewQueryService.getReviewDetail(reviewId, userId);
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
		Page<MyReviewResponse> reviews = reviewQueryService.getMyReviews(userId, reviewType, page, pageSize);
		return DataResponse.from(reviews);
	}

	@Operation(summary = "작성 가능한 리뷰 목록 조회", description = "로그인한 사용자가 작성 가능한 리뷰 목록을 조회합니다.")
	@GetMapping("/api/users/me/reviews/pending")
	public DataResponse<List<PendingReviewResponse>> getPendingReviews(
		@AuthenticationPrincipal Long userId
	) {
		List<PendingReviewResponse> pendingReviews = reviewQueryService.getPendingReviews(userId);
		return DataResponse.from(pendingReviews);
	}

	@Operation(summary = "미작성 리뷰 개수 조회", description = "토큰 필요. 마이페이지 리뷰관리 뱃지에 표시할 미작성 리뷰 개수를 조회합니다.")
	@GetMapping("/api/users/me/reviews/pending/count")
	public DataResponse<PendingReviewCountResponse> getPendingReviewCount(
		@AuthenticationPrincipal Long userId
	) {
		int count = reviewQueryService.getPendingReviewCount(userId);
		PendingReviewCountResponse response = PendingReviewCountResponse.builder()
			.pendingReviewCount(count)
			.build();
		return DataResponse.from(response);
	}

	@Operation(summary = "리뷰 도움돼요 토글", description = "리뷰의 도움돼요를 토글합니다. 이미 눌렀으면 취소, 안 눌렀으면 추가됩니다.")
	@PostMapping("/api/reviews/{reviewId}/helpful")
	public DataResponse<ReviewHelpfulResponse> toggleHelpful(
		@PathVariable Long reviewId,
		@AuthenticationPrincipal Long userId
	) {
		ReviewHelpfulResponse response = reviewCommandService.toggleHelpful(reviewId, userId);
		return DataResponse.from(response);
	}

	@Operation(summary = "리뷰 작성 시작(ID 발급)", description = "리뷰 작성 진입 시 DRAFT 상태의 리뷰 ID를 미리 발급받습니다.")
	@PostMapping("/api/reviews/init")
	public DataResponse<ReviewIdResponse> initializeReview(
		@AuthenticationPrincipal Long userId,
		@RequestParam Long orderItemId
	) {
		Long reviewId = reviewCommandService.initializeReview(userId, orderItemId);
		return DataResponse.from(new ReviewIdResponse(reviewId));
	}

	@Operation(summary = "리뷰 작성 1단계", description = "별점, 착용감 등 1차 답변을 기입합니다. 이전 단계 수정 시에도 사용됩니다.")
	@PatchMapping("/api/reviews/{reviewId}/step1")
	public DataResponse<ReviewStep1Response> updateReviewStep1(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long reviewId,
		@Valid @RequestBody ReviewStep1Request request
	) {
		ReviewStep1Response response = reviewCommandService.updateReviewStep1(userId, reviewId, request);
		return DataResponse.from(response);
	}

	@Operation(summary = "리뷰 작성 2단계 - 사이즈", description = "사이즈 2차 질문(불편 부위)에 답변합니다.")
	@PatchMapping("/api/reviews/{reviewId}/step2/size")
	public DataResponse<Void> updateReviewStep2Size(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long reviewId,
		@Valid @RequestBody ReviewStep2SizeRequest request
	) {
		reviewCommandService.updateReviewStep2Size(userId, reviewId, request);
		return DataResponse.ok();
	}

	@Operation(summary = "리뷰 작성 2단계 - 소재", description = "소재 2차 질문(소재 특징)에 답변합니다.")
	@PatchMapping("/api/reviews/{reviewId}/step2/material")
	public DataResponse<Void> updateReviewStep2Material(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long reviewId,
		@Valid @RequestBody ReviewStep2MaterialRequest request
	) {
		reviewCommandService.updateReviewStep2Material(userId, reviewId, request);
		return DataResponse.ok();
	}

	@Operation(summary = "리뷰 작성 3단계 - 사이즈 AI 생성", description = "사이즈 관련 AI 리뷰를 생성합니다.")
	@GetMapping("/api/reviews/{reviewId}/ai/size")
	public DataResponse<AiReviewResponse> generateSizeAiReview(
		@PathVariable Long reviewId,
		@AuthenticationPrincipal Long userId
	) {
		AiReviewResponse response = reviewCommandService.generateSizeAiReview(userId, reviewId);
		return DataResponse.from(response);
	}

	@Operation(summary = "리뷰 작성 3단계 - 소재 AI 생성", description = "소재 관련 AI 리뷰를 생성합니다.")
	@GetMapping("/api/reviews/{reviewId}/ai/material")
	public DataResponse<AiReviewResponse> generateMaterialAiReview(
		@PathVariable Long reviewId,
		@AuthenticationPrincipal Long userId
	) {
		AiReviewResponse response = reviewCommandService.generateMaterialAiReview(userId, reviewId);
		return DataResponse.from(response);
	}

	@Operation(summary = "리뷰 사진 업로드", description = "리뷰용 사진을 최대 5장까지 S3에 업로드합니다.")
	@PostMapping(value = "/api/reviews/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public DataResponse<List<String>> uploadReviewImages(
		@RequestPart("images") List<MultipartFile> images
	) {
		if (images.size() > 5) {
			throw new ValidationException(ErrorCode.INVALID_PARAMETER);
		}

		List<String> imageUrls = images.stream()
			.map(s3ImageService::uploadReviewImage)
			.toList();
		return DataResponse.from(imageUrls);
	}

	@Operation(summary = "리뷰 최종 제출", description = "최종 리뷰 문장들과 사진을 저장하고 상태를 COMPLETED로 변경합니다.")
	@PostMapping("/api/reviews/{reviewId}/submit")
	public DataResponse<Void> submitReview(
		@AuthenticationPrincipal Long userId,
		@PathVariable Long reviewId,
		@Valid @RequestBody ReviewFinalSubmitRequest request
	) {
		reviewCommandService.submitReview(userId, reviewId, request);
		return DataResponse.ok();
	}

}
