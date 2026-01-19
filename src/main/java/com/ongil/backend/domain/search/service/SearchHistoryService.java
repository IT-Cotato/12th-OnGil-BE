package com.ongil.backend.domain.search.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.search.converter.SearchHistoryConverter;
import com.ongil.backend.domain.search.dto.response.SearchHistoryResponse;
import com.ongil.backend.domain.search.entity.SearchHistory;
import com.ongil.backend.domain.search.repository.SearchHistoryRepository;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchHistoryService {

	private final SearchHistoryRepository searchHistoryRepository;
	private final UserRepository userRepository;
	private final SearchHistoryConverter searchHistoryConverter;

	private static final int MAX_SEARCH_HISTORY = 20;

	/**
	 * 검색 기록 저장
	 * - 같은 키워드를 다시 검색하면 새로운 기록으로 추가
	 * - 이를 통해 최근 검색순을 유지
	 */
	@Transactional
	public void saveSearchHistory(Long userId, String keyword) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

		// 키워드 정규화 (앞뒤 공백 제거, 소문자 변환)
		String normalizedKeyword = keyword.trim();

		if (normalizedKeyword.isEmpty()) {
			return;
		}

		// 새로운 검색 기록 생성
		SearchHistory searchHistory = SearchHistory.builder()
			.user(user)
			.keyword(normalizedKeyword)
			.build();

		searchHistoryRepository.save(searchHistory);

		log.debug("검색 기록 저장 완료 - userId: {}, keyword: {}", userId, normalizedKeyword);
	}

	/**
	 * 최근 검색 기록 조회
	 * - 최대 20개까지 조회
	 * - 중복 키워드 제거 (가장 최근 것만)
	 * - 최신순 정렬
	 */
	public List<SearchHistoryResponse> getRecentSearchHistory(Long userId) {
		Pageable pageable = PageRequest.of(0, MAX_SEARCH_HISTORY);
		List<SearchHistory> histories = searchHistoryRepository.findRecentSearchKeywords(userId, pageable);
		return searchHistoryConverter.toResponseList(histories);
	}

	/**
	 * 모든 검색 기록 삭제
	 */
	@Transactional
	public void deleteAllSearchHistory(Long userId) {
		searchHistoryRepository.deleteAllByUserId(userId);
		log.debug("모든 검색 기록 삭제 완료 - userId: {}", userId);
	}

	/**
	 * 특정 키워드의 검색 기록 삭제
	 */
	@Transactional
	public void deleteSearchHistory(Long userId, String keyword) {
		String normalizedKeyword = keyword.trim();
		searchHistoryRepository.deleteByUserIdAndKeyword(userId, normalizedKeyword);
		log.debug("검색 기록 삭제 완료 - userId: {}, keyword: {}", userId, normalizedKeyword);
	}
}
