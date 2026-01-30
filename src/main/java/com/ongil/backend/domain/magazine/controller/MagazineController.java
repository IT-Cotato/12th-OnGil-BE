package com.ongil.backend.domain.magazine.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.magazine.dto.request.CommentReqDto;
import com.ongil.backend.domain.magazine.dto.response.CommentResDto;
import com.ongil.backend.domain.magazine.dto.response.MagazineResDto;
import com.ongil.backend.domain.magazine.enums.MagazineCategory;
import com.ongil.backend.domain.magazine.service.MagazineCommentService;
import com.ongil.backend.domain.magazine.service.MagazineCrawlingService;
import com.ongil.backend.domain.magazine.service.MagazineService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/magazines")
@RequiredArgsConstructor
@Tag(name = "Magazine", description = "매거진 API")
public class MagazineController {

	private final MagazineService magazineService;
	private final MagazineCommentService commentService;
	private final MagazineCrawlingService crawlingService;

	@GetMapping("/recommend")
	@Operation(summary = "추천 매거진 조회", description = "임의 매거진 6개를 반환합니다.")
	public DataResponse<List<MagazineResDto>> getRecommended() {
		List<MagazineResDto> response = magazineService.getRecommendedMagazines();
		return DataResponse.from(response);
	}

	@GetMapping
	@Operation(summary = "주제별 매거진 조회", description = "주제별 매거진을 조회합니다. 기본값은 가격")
	public DataResponse<List<MagazineResDto>> getByCategory(
		@RequestParam(required = false, defaultValue = "PRICE") MagazineCategory category) {
		List<MagazineResDto> response = magazineService.getMagazinesByCategory(category);
		return DataResponse.from(response);
	}

	@GetMapping("/{magazineId}")
	@Operation(summary = "매거진 상세 조회", description = "특정 ID의 매거진 상세 내용을 조회하고 조회수를 1 증가시킵니다.")
	public DataResponse<MagazineResDto> getMagazineDetail(@PathVariable Long magazineId) {
		MagazineResDto response = magazineService.getMagazineDetail(magazineId);
		return DataResponse.from(response);
	}

	@PostMapping("/{magazineId}/bookmark")
	@Operation(summary = "매거진 저장 토글", description = "해당 매거진을 저장하거나 저장 취소 합니다. true: 저장 성공 의미(토큰 필요)")
	public DataResponse<Boolean> toggleBookmark(
		@PathVariable Long magazineId, @AuthenticationPrincipal Long userId ) {
		boolean isBookmarked = magazineService.toggleBookmark(magazineId, userId);
		return DataResponse.from(isBookmarked);
	}

	@GetMapping("/bookmarks")
	@Operation(summary = "저장된 매거진 목록 조회", description = "카테고리 필터가 가능하며 기본값은 가격 카테고리입니다. (토큰 필요)")
	public DataResponse<List<MagazineResDto>> getBookmarkedMagazines(
		@AuthenticationPrincipal Long userId,
		@RequestParam(required = false) MagazineCategory category) {
		List<MagazineResDto> response = magazineService.getBookmarkedMagazines(userId, category);
		return DataResponse.from(response);
	}

	@PostMapping("/comments/{magazineId}")
	@Operation(summary = "댓글 등록", description = "특정 매거진에 댓글을 등록하고 작성된 댓글 정보를 반환합니다. (토큰필요)")
	public DataResponse<CommentResDto> createComment(
		@PathVariable Long magazineId, @AuthenticationPrincipal Long userId, @RequestBody CommentReqDto request) {
		CommentResDto response = commentService.createComment(magazineId, userId, request.content());
		return DataResponse.from(response);
	}

	@GetMapping("/comments/{magazineId}")
	@Operation(summary = "댓글 조회", description = "특정 매거진의 댓글을 조회합니다.")
	public DataResponse<List<CommentResDto>> getComments(
		@PathVariable Long magazineId) {
		List<CommentResDto> response = commentService.getComments(magazineId);
		return DataResponse.from(response);
	}

	@PostMapping("/{commentId}/like")
	@Operation(summary = "댓글 공감 토글", description = "도움 됐어요 기능을 토글합니다. true: 공감 성공 의미 (토큰 필요)")
	public DataResponse<Boolean> toggleLike(
		@PathVariable Long commentId,
		@AuthenticationPrincipal Long userId) {
		boolean isLiked = commentService.toggleCommentLike(commentId, userId);
		return DataResponse.from(isLiked);
	}

	@Hidden
	@PostMapping("/crawl")
	@Operation(summary = "매거진 수동 크롤링", description = "매거진 데이터가 부족할 경우를 위한 api")
	public DataResponse<String> manualCrawl(@RequestParam(required = false) MagazineCategory category) {
		CompletableFuture.runAsync(() -> {
			if (category != null) {
				crawlingService.crawlAndSave(category);
			} else {
				// 카테고리 미지정 시 전체 크롤링
				for (MagazineCategory cat : MagazineCategory.values()) {
					crawlingService.crawlAndSave(cat);
				}
			}
		});

		return DataResponse.from("수동 크롤링 시작");
	}
}
