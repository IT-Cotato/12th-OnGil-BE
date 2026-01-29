package com.ongil.backend.domain.search.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.product.dto.request.ProductSearchCondition;
import com.ongil.backend.domain.product.dto.response.ProductSearchPageResDto;
import com.ongil.backend.domain.product.enums.ProductSortType;
import com.ongil.backend.domain.product.service.ProductService;
import com.ongil.backend.domain.search.dto.response.VoiceSearchResDto;
import com.ongil.backend.domain.search.service.AiSearchService;
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
	private final ProductService productService;
	private final AiSearchService aiSearchService;

	@GetMapping("/autocomplete")
	@Operation(summary = "검색어 자동 완성", description = "사용자가 입력 중인 검색어에 대한 연관 검색어를 반환합니다.(브랜드, 카테고리)")
	public ResponseEntity<DataResponse<List<String>>> autocomplete(
		@RequestParam String query) {
		List<String> suggestions = searchService.getAutocomplete(query);
		return ResponseEntity.ok(DataResponse.from(suggestions));
	}

	@GetMapping("/recommend")
	@Operation(summary = "추천 검색어 조회", description = "현재 쇼핑몰의 인기 검색어 리스트를 반환합니다.")
	public ResponseEntity<DataResponse<List<String>>> getRecommend() {
		return ResponseEntity.ok(DataResponse.from(searchService.getTopKeywords()));
	}

	@GetMapping("/recent")
	@Operation(summary = "최근 검색어 조회", description = "로그인한 사용자의 최근 검색어 목록을 반환합니다. (토큰 필요)")
	public ResponseEntity<DataResponse<List<String>>> getRecent(
		@AuthenticationPrincipal Long userId) {
		if (userId == null) {
			return ResponseEntity.ok(DataResponse.from(Collections.emptyList()));
		}
		List<String> recentSearches = recentSearchService.getRecentSearches(userId);
		return ResponseEntity.ok(DataResponse.from(recentSearches));
	}

	@DeleteMapping("/recent")
	@Operation(summary = "최근 검색어 개별 삭제", description = "목록에서 특정 키워드를 삭제합니다. (토큰 필요)")
	public ResponseEntity<DataResponse<Void>> removeRecent(
		@AuthenticationPrincipal Long userId,
		@RequestParam String keyword) {
		if (userId != null) {
			recentSearchService.removeRecentSearch(userId, keyword);
		}
		return ResponseEntity.ok(DataResponse.from(null));
	}

	@DeleteMapping("/recent/all")
	@Operation(summary = "최근 검색어 전체 삭제", description = "사용자의 최근 검색어 기록을 모두 초기화합니다. (토큰 필요)")
	public ResponseEntity<DataResponse<Void>> clearAllRecent(
		@AuthenticationPrincipal Long userId) {
		if (userId != null) {
			recentSearchService.clearRecentSearches(userId);
		}
		return ResponseEntity.ok(DataResponse.from(null));
	}

	@PostMapping("/voice")
	@Operation(summary = "음성 검색", description = "음성 문장에서 키워드를 추출하여 상품을 검색합니다."
	)
	public ResponseEntity<DataResponse<VoiceSearchResDto>> voiceSearch(
		@RequestParam String speechText,
		@PageableDefault(size = 20) Pageable pageable
	) {
		String extractedKeyword = aiSearchService.extractKeywords(speechText);

		ProductSearchPageResDto searchResult = productService.getProducts(
			ProductSearchCondition.builder().build(),
			ProductSortType.POPULAR,
			pageable,
			extractedKeyword,
			null
		);

		return ResponseEntity.ok(DataResponse.from(
			new VoiceSearchResDto(extractedKeyword, searchResult)
		));
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