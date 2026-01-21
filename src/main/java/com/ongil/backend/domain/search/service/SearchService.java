package com.ongil.backend.domain.search.service;

import com.ongil.backend.domain.search.converter.SearchConverter;
import com.ongil.backend.domain.search.dto.response.SearchAutocompleteResponse;
import com.ongil.backend.domain.search.dto.response.SearchLogResponse;
import com.ongil.backend.domain.search.repository.SearchRepository;
import com.ongil.backend.domain.search.util.SynonymProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 검색 관련 비즈니스 로직을 처리하는 서비스
 * - Redis를 활용한 최근 검색어/인기 검색어 관리
 * - DB를 활용한 카테고리/브랜드/상품 자동완성 기능 제공
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SearchRepository searchRepository;
    private final SynonymProvider synonymProvider;
    private final SearchConverter searchConverter;

    // [Refactor] Redis Key 상수로 추출
    private static final String RECENT_KEY_PREFIX = "search:recent:";
    private static final String POPULAR_KEY = "search:popular";

    // [Refactor] 매직 넘버 상수로 추출
    private static final int MAX_RECENT_LOG_SIZE = 7;      // 최근 검색어 최대 저장 개수
    private static final int MAX_POPULAR_LOG_SIZE = 5;     // 인기 검색어 최대 노출 개수
    private static final int MAX_AUTOCOMPLETE_SIZE = 12;   // 자동완성 최대 노출 개수

    /**
     * 검색어 기록 저장
     * 1. 전역 인기 검색어 점수 증가 (ZSet)
     * 2. 로그인 유저의 경우 최근 검색어 저장 및 개수 제한 관리 (Sliding Window)
     *
     * @param userId  로그인한 사용자 ID (비로그인 시 null)
     * @param keyword 검색 키워드
     */
    @Transactional
    public void saveSearchLog(Long userId, String keyword) {
        if (keyword == null || keyword.isBlank()) return;

        // 1. 인기 검색어 점수 증가
        redisTemplate.opsForZSet().incrementScore(POPULAR_KEY, keyword, 1.0);

        // 2. 로그인 유저의 경우 최근 검색어 관리
        if (userId != null) {
            String key = RECENT_KEY_PREFIX + userId;
            // 최신순 정렬을 위해 현재 시간을 Score로 사용
            redisTemplate.opsForZSet().add(key, keyword, System.currentTimeMillis());

            // 개수 제한 로직 (오래된 검색어 삭제)
            Long size = redisTemplate.opsForZSet().size(key);
            if (size != null && size > MAX_RECENT_LOG_SIZE) {
                // 0부터 (현재개수 - 유지개수 - 1) 까지 삭제
                redisTemplate.opsForZSet().removeRange(key, 0, size - MAX_RECENT_LOG_SIZE - 1);
            }
        }
    }

    /**
     * 검색 초기 화면 데이터 조회
     * - 로그인 유저 & 기록 있음: 최근 검색어 반환
     * - 그 외: 인기 검색어 반환
     *
     * @param userId 로그인한 사용자 ID
     * @return 검색어 리스트 응답 DTO
     */
    public List<SearchLogResponse> getInitialSearchLog(Long userId) {
        // 1. 로그인 유저의 최근 검색어 조회
        if (userId != null) {
            String key = RECENT_KEY_PREFIX + userId;
            Set<String> recentKeywords = redisTemplate.opsForZSet().reverseRange(key, 0, MAX_RECENT_LOG_SIZE - 1);

            if (recentKeywords != null && !recentKeywords.isEmpty()) {
                return recentKeywords.stream()
                        .map(searchConverter::toSearchLogResponse)
                        .collect(Collectors.toList());
            }
        }

        // 2. 최근 검색어가 없거나 비로그인 시 인기 검색어 조회
        Set<String> popularKeywords = redisTemplate.opsForZSet().reverseRange(POPULAR_KEY, 0, MAX_POPULAR_LOG_SIZE - 1);
        if (popularKeywords == null) return Collections.emptyList();

        return popularKeywords.stream()
                .map(searchConverter::toSearchLogResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 최근 검색어 개별 삭제
     *
     * @param userId  사용자 ID
     * @param keyword 삭제할 키워드
     */
    @Transactional
    public void deleteRecentSearch(Long userId, String keyword) {
        if (userId == null) return;
        redisTemplate.opsForZSet().remove(RECENT_KEY_PREFIX + userId, keyword);
    }

    /**
     * 최근 검색어 전체 삭제
     *
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteAllRecentSearch(Long userId) {
        if (userId == null) return;
        redisTemplate.delete(RECENT_KEY_PREFIX + userId);
    }

    /**
     * 실시간 검색어 자동완성
     * - 우선순위: 카테고리 > 브랜드 > 상품(상품명, 색상)
     * - 최대 개수 제한(MAX_AUTOCOMPLETE_SIZE)에 맞춰 순차적으로 채움
     *
     * @param keyword 사용자 입력 키워드
     * @return 자동완성 결과 리스트
     */
    public List<SearchAutocompleteResponse> getAutocomplete(String keyword) {
        // [Critical Fix] 빈 검색어 방어 로직 (CodeRabbit 지적 반영)
        if (keyword == null || keyword.isBlank()) {
            return Collections.emptyList();
        }

        // 1. 검색어 정제 (동의어 처리 & 공백 제거)
        String refinedKeyword = synonymProvider.getRefinedKeyword(keyword);

        // 정제 후에도 비어있을 수 있으므로 재검사
        if (refinedKeyword == null || refinedKeyword.isBlank()) {
            return Collections.emptyList();
        }

        String searchKeyword = refinedKeyword.replaceAll("\\s+", "");
        if (searchKeyword.isBlank()) {
            return Collections.emptyList();
        }

        List<SearchAutocompleteResponse> result = new ArrayList<>();

        // 2. 카테고리 검색
        List<Object[]> categoryResults = searchRepository.searchCategories(searchKeyword, MAX_AUTOCOMPLETE_SIZE);
        for (Object[] row : categoryResults) {
            result.add(SearchAutocompleteResponse.builder()
                    .id(((Number) row[0]).longValue())
                    .name((String) row[1])
                    .type("CATEGORY")
                    .build());
        }
        if (result.size() >= MAX_AUTOCOMPLETE_SIZE) return result;

        // 3. 브랜드 검색 (남은 개수만큼)
        int remainForBrand = MAX_AUTOCOMPLETE_SIZE - result.size();
        List<Object[]> brandResults = searchRepository.searchBrands(searchKeyword, remainForBrand);
        for (Object[] row : brandResults) {
            result.add(SearchAutocompleteResponse.builder()
                    .id(((Number) row[0]).longValue())
                    .name((String) row[1])
                    .type("BRAND")
                    .build());
        }
        if (result.size() >= MAX_AUTOCOMPLETE_SIZE) return result;

        // 4. 상품 & 색상 검색 (남은 개수만큼)
        int remainForProduct = MAX_AUTOCOMPLETE_SIZE - result.size();
        List<Object[]> productResults = searchRepository.searchProducts(searchKeyword, remainForProduct);
        for (Object[] row : productResults) {
            result.add(SearchAutocompleteResponse.builder()
                    .id(((Number) row[0]).longValue())
                    .name((String) row[1])
                    .type("PRODUCT")
                    .build());
        }

        return result;
    }
}