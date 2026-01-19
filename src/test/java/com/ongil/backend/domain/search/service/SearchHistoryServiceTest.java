package com.ongil.backend.domain.search.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.ongil.backend.domain.search.converter.SearchHistoryConverter;
import com.ongil.backend.domain.search.dto.response.SearchHistoryResponse;
import com.ongil.backend.domain.search.entity.SearchHistory;
import com.ongil.backend.domain.search.repository.SearchHistoryRepository;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class SearchHistoryServiceTest {

	@Mock
	private SearchHistoryRepository searchHistoryRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private SearchHistoryConverter searchHistoryConverter;

	@InjectMocks
	private SearchHistoryService searchHistoryService;

	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = User.builder()
			.id(1L)
			.name("Test User")
			.email("test@example.com")
			.build();
	}

	@Test
	@DisplayName("검색 기록 저장 - 성공")
	void saveSearchHistory_Success() {
		// given
		String keyword = "나이키";
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
		when(searchHistoryRepository.save(any(SearchHistory.class))).thenReturn(
			SearchHistory.builder()
				.user(testUser)
				.keyword(keyword)
				.build()
		);

		// when
		assertDoesNotThrow(() -> searchHistoryService.saveSearchHistory(1L, keyword));

		// then
		verify(userRepository, times(1)).findById(1L);
		verify(searchHistoryRepository, times(1)).save(any(SearchHistory.class));
	}

	@Test
	@DisplayName("검색 기록 저장 - 빈 키워드는 저장하지 않음")
	void saveSearchHistory_EmptyKeyword() {
		// given
		String keyword = "   ";
		when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

		// when
		searchHistoryService.saveSearchHistory(1L, keyword);

		// then
		verify(searchHistoryRepository, never()).save(any(SearchHistory.class));
	}

	@Test
	@DisplayName("검색 기록 저장 - 사용자 없음")
	void saveSearchHistory_UserNotFound() {
		// given
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

		// when & then
		assertThrows(EntityNotFoundException.class, 
			() -> searchHistoryService.saveSearchHistory(1L, "keyword"));
	}

	@Test
	@DisplayName("최근 검색 기록 조회 - 성공")
	void getRecentSearchHistory_Success() {
		// given
		List<SearchHistory> histories = Arrays.asList(
			SearchHistory.builder().user(testUser).keyword("나이키").build(),
			SearchHistory.builder().user(testUser).keyword("아디다스").build()
		);
		
		List<SearchHistoryResponse> responses = Arrays.asList(
			SearchHistoryResponse.builder().keyword("나이키").build(),
			SearchHistoryResponse.builder().keyword("아디다스").build()
		);

		when(searchHistoryRepository.findRecentSearchKeywords(anyLong(), any(Pageable.class)))
			.thenReturn(histories);
		when(searchHistoryConverter.toResponseList(histories)).thenReturn(responses);

		// when
		List<SearchHistoryResponse> result = searchHistoryService.getRecentSearchHistory(1L);

		// then
		assertNotNull(result);
		assertEquals(2, result.size());
		verify(searchHistoryRepository, times(1)).findRecentSearchKeywords(anyLong(), any(Pageable.class));
	}

	@Test
	@DisplayName("모든 검색 기록 삭제 - 성공")
	void deleteAllSearchHistory_Success() {
		// when
		assertDoesNotThrow(() -> searchHistoryService.deleteAllSearchHistory(1L));

		// then
		verify(searchHistoryRepository, times(1)).deleteAllByUserId(1L);
	}

	@Test
	@DisplayName("특정 검색 기록 삭제 - 성공")
	void deleteSearchHistory_Success() {
		// given
		String keyword = "나이키";

		// when
		assertDoesNotThrow(() -> searchHistoryService.deleteSearchHistory(1L, keyword));

		// then
		verify(searchHistoryRepository, times(1)).deleteByUserIdAndKeyword(1L, keyword);
	}
}
