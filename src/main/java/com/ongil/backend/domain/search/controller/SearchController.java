package com.ongil.backend.domain.search.controller;

import com.ongil.backend.domain.search.dto.response.SearchAutocompleteResponse;
import com.ongil.backend.domain.search.dto.response.SearchLogResponse;
import com.ongil.backend.domain.search.service.SearchService;
import com.ongil.backend.global.common.dto.DataResponse; // DataResponse 사용
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "Search", description = "검색 관련 API")
@Validated
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "검색 초기 화면 데이터", description = "로그인: 최근 검색어(7개) / 비로그인: 추천 검색어(5개)")
    @GetMapping("/logs")
    public DataResponse<List<SearchLogResponse>> getSearchLogs(
            @AuthenticationPrincipal Long userId) { // UserDetails 대신 Long userId 바로 사용

        List<SearchLogResponse> response = searchService.getInitialSearchLog(userId);
        return DataResponse.from(response);
    }

    @Operation(summary = "검색어 기록 저장", description = "검색 버튼 클릭 시 호출 (상품 목록 이동 전)")
    @PostMapping("/log")
    public DataResponse<Void> saveSearchLog(
            @AuthenticationPrincipal Long userId,
            @RequestParam String keyword) {

        searchService.saveSearchLog(userId, keyword);
        return DataResponse.from(null);
    }

    @Operation(summary = "최근 검색어 개별 삭제")
    @DeleteMapping("/log")
    public DataResponse<Void> deleteSearchLog(
            @AuthenticationPrincipal Long userId,
            @RequestParam String keyword) {

        searchService.deleteRecentSearch(userId, keyword);
        return DataResponse.from(null);
    }

    @Operation(summary = "최근 검색어 전체 삭제")
    @DeleteMapping("/logs")
    public DataResponse<Void> deleteAllSearchLogs(
            @AuthenticationPrincipal Long userId) {

        searchService.deleteAllRecentSearch(userId);
        return DataResponse.from(null);
    }

    @Operation(summary = "실시간 자동완성", description = "카테고리 & 브랜드 검색 (우선순위: 카테고리)")
    @GetMapping("/autocomplete")
    public DataResponse<List<SearchAutocompleteResponse>> getAutocomplete(@RequestParam String keyword) {
        List<SearchAutocompleteResponse> response = searchService.getAutocomplete(keyword);
        return DataResponse.from(response);
    }
}