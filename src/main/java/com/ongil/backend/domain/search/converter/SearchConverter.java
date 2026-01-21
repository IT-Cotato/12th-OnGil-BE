package com.ongil.backend.domain.search.converter;

import org.springframework.stereotype.Component;
import com.ongil.backend.domain.search.dto.response.SearchAutocompleteResponse;
import com.ongil.backend.domain.search.dto.response.SearchLogResponse;

@Component
public class SearchConverter {

    // Redis 문자열 -> 응답 DTO 변환
    public SearchLogResponse toSearchLogResponse(String keyword) {
        return SearchLogResponse.builder()
                .keyword(keyword)
                .build();
    }

    // DB 데이터 -> 응답 DTO 변환
    public SearchAutocompleteResponse toAutocompleteResponse(Long id, String name, String type) {
        return SearchAutocompleteResponse.builder()
                .id(id)
                .name(name)
                .type(type)
                .build();
    }
}