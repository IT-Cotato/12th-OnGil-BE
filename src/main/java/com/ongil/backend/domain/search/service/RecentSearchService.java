package com.ongil.backend.domain.search.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecentSearchService {

	private final RedisTemplate<String, String> redisTemplate;
	private static final String RECENT_SEARCH_KEY = "recent_search:";
	private static final int MAX_RECENT_COUNT = 7;

	@Async
	public void saveRecentSearch(Long userId, String keyword) {
		String key = RECENT_SEARCH_KEY + userId;
		double score = System.currentTimeMillis();

		redisTemplate.opsForZSet().add(key, keyword, score);

		Long size = redisTemplate.opsForZSet().size(key);
		if (size != null && size > MAX_RECENT_COUNT) {
			redisTemplate.opsForZSet().removeRange(key, 0, size - MAX_RECENT_COUNT - 1);
		}
	}

	public List<String> getRecentSearches(Long userId) {
		String key = RECENT_SEARCH_KEY + userId;

		Set<String> searches = redisTemplate.opsForZSet().reverseRange(key, 0, MAX_RECENT_COUNT - 1);

		return searches != null ? new ArrayList<>(searches) : List.of();
	}

	public void removeRecentSearch(Long userId, String keyword) {
		String key = RECENT_SEARCH_KEY + userId;
		redisTemplate.opsForZSet().remove(key, keyword);
	}

	public void clearRecentSearches(Long userId) {
		String key = RECENT_SEARCH_KEY + userId;
		redisTemplate.delete(key);
	}

}
