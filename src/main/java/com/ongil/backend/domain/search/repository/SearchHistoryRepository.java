package com.ongil.backend.domain.search.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ongil.backend.domain.search.entity.SearchHistory;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

	// 사용자의 최근 검색 기록 조회 (중복 제거, 최신순)
	@Query("""
		SELECT sh FROM SearchHistory sh
		WHERE sh.user.id = :userId
		  AND sh.keyword IN (
		    SELECT sh2.keyword FROM SearchHistory sh2
		    WHERE sh2.user.id = :userId
		    GROUP BY sh2.keyword
		  )
		  AND sh.id IN (
		    SELECT MAX(sh3.id) FROM SearchHistory sh3
		    WHERE sh3.user.id = :userId
		    GROUP BY sh3.keyword
		  )
		ORDER BY sh.createdAt DESC
		""")
	List<SearchHistory> findRecentSearchKeywords(@Param("userId") Long userId, Pageable pageable);

	// 특정 사용자의 특정 키워드 검색 기록 찾기
	Optional<SearchHistory> findTopByUserIdAndKeywordOrderByCreatedAtDesc(Long userId, String keyword);

	// 사용자의 모든 검색 기록 삭제
	@Modifying
	@Query("DELETE FROM SearchHistory sh WHERE sh.user.id = :userId")
	void deleteAllByUserId(@Param("userId") Long userId);

	// 사용자의 특정 키워드 검색 기록 삭제
	@Modifying
	@Query("DELETE FROM SearchHistory sh WHERE sh.user.id = :userId AND sh.keyword = :keyword")
	void deleteByUserIdAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);
}
