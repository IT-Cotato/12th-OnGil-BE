package com.ongil.backend.global.config.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {

	private final RedisTemplate<String, Object> redisTemplate;

	// 캐시 저장
	public void save(String key, Object value, long ttlHours) {
		try {
			if (ttlHours <= 0) {
				redisTemplate.opsForValue().set(key, value);
			} else {
				redisTemplate.opsForValue().set(key, value, ttlHours, TimeUnit.HOURS);
			}
		} catch (Exception e) {
			log.warn("Redis save 실패 - key: {}, error: {}", key, e.getMessage());
		}
	}

	// 리스트 형태의 캐시 조회
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(String key, Class<T> elementType) {
		try {
			Object cached = redisTemplate.opsForValue().get(key);
			if (cached instanceof List) {
				// ObjectMapper를 매번 생성 (Bean 충돌 방지)
				ObjectMapper mapper = new ObjectMapper();
				List<T> result = new ArrayList<>();

				for (Object item : (List<?>)cached) {
					T converted = mapper.convertValue(item, elementType);
					result.add(converted);
				}
				return result;
			}
			return null;
		} catch (Exception e) {
			log.warn("Redis getList 실패 - key: {}, error: {}", key, e.getMessage());
			return null;
		}
	}

	// 캐시 삭제
	public void delete(String key) {
		try {
			redisTemplate.delete(key);
		} catch (Exception e) {
			log.warn("Redis delete 실패 - key: {}", key);
		}
	}
}