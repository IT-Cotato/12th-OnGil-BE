package com.ongil.backend.global.config.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisCacheService {

	private final RedisTemplate<String, Object> redisTemplate;

	public void save(String key, Object value, long ttlHours) {
		try {
			if (ttlHours <= 0) {
				redisTemplate.opsForValue().set(key, value);
			} else {
				redisTemplate.opsForValue().set(key, value, ttlHours, TimeUnit.HOURS);
			}
		} catch (Exception e) {
			// Redis 장애 시에도 서비스는 정상 동작
		}
	}

	public <T> T get(String key, Class<T> clazz) {
		try {
			Object cached = redisTemplate.opsForValue().get(key);
			if (cached != null) {
				return clazz.cast(cached);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public void delete(String key) {
		try {
			redisTemplate.delete(key);
		} catch (Exception e) {
			// 삭제 실패해도 TTL로 자동 삭제됨
		}
	}

	public boolean tryLock(String lockKey, long timeoutSeconds) {
		try {
			Boolean success = redisTemplate.opsForValue()
				.setIfAbsent(lockKey, "LOCKED", timeoutSeconds, TimeUnit.SECONDS);
			return Boolean.TRUE.equals(success);
		} catch (Exception e) {
			return false;
		}
	}

	public void unlock(String lockKey) {
		try {
			redisTemplate.delete(lockKey);
		} catch (Exception e) {
			// 해제 실패해도 TTL로 자동 해제됨
		}
	}

	public boolean waitForLock(String lockKey, long timeoutSeconds, long maxWaitSeconds) {
		long startTime = System.currentTimeMillis();
		long maxWaitMillis = maxWaitSeconds * 1000;

		while (System.currentTimeMillis() - startTime < maxWaitMillis) {
			if (tryLock(lockKey, timeoutSeconds)) {
				return true;
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}

		return false;
	}
}