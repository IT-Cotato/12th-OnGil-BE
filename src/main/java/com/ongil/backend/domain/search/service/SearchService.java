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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SearchRepository searchRepository;
    private final SynonymProvider synonymProvider;
    private final SearchConverter searchConverter;

    private static final String RECENT_KEY = "search:recent:";
    private static final String POPULAR_KEY = "search:popular";

    @Transactional
    public void saveSearchLog(Long userId, String keyword) {
        if (keyword == null || keyword.isBlank()) return;
        redisTemplate.opsForZSet().incrementScore(POPULAR_KEY, keyword, 1.0);
        if (userId != null) {
            String key = RECENT_KEY + userId;
            redisTemplate.opsForZSet().add(key, keyword, System.currentTimeMillis());
            Long size = redisTemplate.opsForZSet().size(key);
            if (size != null && size > 7) {
                redisTemplate.opsForZSet().removeRange(key, 0, size - 8);
            }
        }
    }

    public List<SearchLogResponse> getInitialSearchLog(Long userId) {
        if (userId != null) {
            String key = RECENT_KEY + userId;
            Set<String> recentKeywords = redisTemplate.opsForZSet().reverseRange(key, 0, 6);
            if (recentKeywords != null && !recentKeywords.isEmpty()) {
                return recentKeywords.stream()
                        .map(searchConverter::toSearchLogResponse)
                        .collect(Collectors.toList());
            }
        }
        Set<String> popularKeywords = redisTemplate.opsForZSet().reverseRange(POPULAR_KEY, 0, 4);
        if (popularKeywords == null) return Collections.emptyList();
        return popularKeywords.stream()
                .map(searchConverter::toSearchLogResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteRecentSearch(Long userId, String keyword) {
        if (userId == null) return;
        redisTemplate.opsForZSet().remove(RECENT_KEY + userId, keyword);
    }

    @Transactional
    public void deleteAllRecentSearch(Long userId) {
        if (userId == null) return;
        redisTemplate.delete(RECENT_KEY + userId);
    }

    public List<SearchAutocompleteResponse> getAutocomplete(String keyword) {
        // 1. 검색어 정제
        String refinedKeyword = synonymProvider.getRefinedKeyword(keyword);
        String searchKeyword = refinedKeyword.replaceAll("\\s+", "");

        List<SearchAutocompleteResponse> result = new ArrayList<>();

        // 2. 카테고리 검색
        List<Object[]> categoryResults = searchRepository.searchCategories(searchKeyword, 12);
        for (Object[] row : categoryResults) {
            result.add(SearchAutocompleteResponse.builder()
                    .id(((Number) row[0]).longValue())
                    .name((String) row[1])
                    .type("CATEGORY")
                    .build());
        }
        if (result.size() >= 12) return result;

        // 3. 브랜드 검색
        int remainForBrand = 12 - result.size();
        List<Object[]> brandResults = searchRepository.searchBrands(searchKeyword, remainForBrand);
        for (Object[] row : brandResults) {
            result.add(SearchAutocompleteResponse.builder()
                    .id(((Number) row[0]).longValue())
                    .name((String) row[1])
                    .type("BRAND")
                    .build());
        }
        if (result.size() >= 12) return result;

        // 4. 상품 & 색상 검색
        int remainForProduct = 12 - result.size();
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