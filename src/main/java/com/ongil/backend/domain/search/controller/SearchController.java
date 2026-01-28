package com.ongil.backend.domain.search.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.search.service.RecentSearchService;
import com.ongil.backend.domain.search.service.SearchIndexingService;
import com.ongil.backend.domain.search.service.SearchService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
@Tag(name = "Search", description = "검색 API")
public class SearchController {

	private final SearchService searchService;
	private final SearchIndexingService searchIndexingService;
	private final RecentSearchService recentSearchService;

	// 자동 완성
	@GetMapping("/autocomplete")
	public ResponseEntity<DataResponse<List<String>>> autocomplete(
		@RequestParam String query) {
		List<String> suggestions = searchService.getAutocomplete(query);
		return ResponseEntity.ok(DataResponse.from(suggestions));
	}

	// 추천 검색어
	@GetMapping("/recommend")
	public ResponseEntity<DataResponse<List<String>>> getRecommend() {
		return ResponseEntity.ok(DataResponse.from(searchService.getTopKeywords()));
	}

	// 최근 검색어
	@GetMapping("/recent")
	@Operation(description = "토큰 필요")
	public ResponseEntity<DataResponse<List<String>>> getRecent(
		@AuthenticationPrincipal Long userId) {
		if (userId == null) {
			return ResponseEntity.ok(DataResponse.from(Collections.emptyList()));
		}
		List<String> recentSearches = recentSearchService.getRecentSearches(userId);
		return ResponseEntity.ok(DataResponse.from(recentSearches));
	}

	// 최근 검색어 개별 삭제
	@DeleteMapping("/recent")
	@Operation(description = "토큰 필요")
	public ResponseEntity<DataResponse<Void>> removeRecent(
		@AuthenticationPrincipal Long userId,
		@RequestParam String keyword) {
		if (userId != null) {
			recentSearchService.removeRecentSearch(userId, keyword);
		}
		return ResponseEntity.ok(DataResponse.from(null));
	}

	// 최근 검색어 전체 삭제
	@DeleteMapping("/recent/all")
	@Operation(description = "토큰 필요")
	public ResponseEntity<DataResponse<Void>> clearAllRecent(
		@AuthenticationPrincipal Long userId) {
		if (userId != null) {
			recentSearchService.clearRecentSearches(userId);
		}
		return ResponseEntity.ok(DataResponse.from(null));
	}

	/**
	 * [관리자용] 데이터 전체 동기화 API
	 */
	@PostMapping("/admin/reindex")
	@Operation(
		summary = "데이터 전체 동기화 (관리자용)",
		description = "데이터베이스의 모든 상품 정보를 Elasticsearch로 다시 색인합니다."
	)
	public ResponseEntity<String> reindex() {
		searchIndexingService.indexAllProducts();
		return ResponseEntity.ok("전체 데이터 색인이 완료되었습니다.");
	}
}