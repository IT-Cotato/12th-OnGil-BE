package com.ongil.backend.domain.search.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.ongil.backend.domain.search.dto.response.SearchHistoryResponse;
import com.ongil.backend.domain.search.service.SearchHistoryService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Search History", description = "검색 기록 관련 API")
@RestController
@RequestMapping("/api/search/history")
@RequiredArgsConstructor
public class SearchHistoryController {

	private final SearchHistoryService searchHistoryService;

	@Operation(summary = "최근 검색 기록 조회", description = "사용자의 최근 검색 기록을 최대 20개까지 조회합니다.")
	@GetMapping
	public DataResponse<List<SearchHistoryResponse>> getSearchHistory(
		@AuthenticationPrincipal Long userId
	) {
		List<SearchHistoryResponse> history = searchHistoryService.getRecentSearchHistory(userId);
		return DataResponse.from(history);
	}

	@Operation(summary = "모든 검색 기록 삭제", description = "사용자의 모든 검색 기록을 삭제합니다.")
	@DeleteMapping
	public DataResponse<Void> deleteAllSearchHistory(
		@AuthenticationPrincipal Long userId
	) {
		searchHistoryService.deleteAllSearchHistory(userId);
		return DataResponse.from(null);
	}

	@Operation(summary = "특정 검색 기록 삭제", description = "사용자의 특정 키워드 검색 기록을 삭제합니다.")
	@DeleteMapping("/{keyword}")
	public DataResponse<Void> deleteSearchHistory(
		@AuthenticationPrincipal Long userId,
		@PathVariable String keyword
	) {
		searchHistoryService.deleteSearchHistory(userId, keyword);
		return DataResponse.from(null);
	}
}
