package com.ongil.backend.domain.search.converter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.search.dto.response.SearchHistoryResponse;
import com.ongil.backend.domain.search.entity.SearchHistory;

@Component
public class SearchHistoryConverter {

	public SearchHistoryResponse toResponse(SearchHistory searchHistory) {
		return SearchHistoryResponse.builder()
			.id(searchHistory.getId())
			.keyword(searchHistory.getKeyword())
			.searchedAt(searchHistory.getCreatedAt())
			.build();
	}

	public List<SearchHistoryResponse> toResponseList(List<SearchHistory> searchHistories) {
		return searchHistories.stream()
			.map(this::toResponse)
			.toList();
	}
}
