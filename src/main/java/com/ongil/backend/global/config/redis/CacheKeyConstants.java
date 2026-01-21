package com.ongil.backend.global.config.redis;

public class CacheKeyConstants {

	// Product AI 분산 락 관련
	public static final String PRODUCT_AI_LOCK_PREFIX = "LOCK:PRODUCT:AI:";
	public static final long PRODUCT_AI_LOCK_TTL_SECONDS = 10;

	// Brand/Category 캐시 관련
	public static final String BRANDS_ALL = "BRANDS:ALL";
	public static final String CATEGORIES_ALL = "CATEGORIES:ALL";
	public static final long MASTER_DATA_TTL_HOURS = 0;  // 무한 TTL

	// 키 생성 메서드
	public static String getProductAiLockKey(Long productId) {
		return PRODUCT_AI_LOCK_PREFIX + productId;
	}

	private CacheKeyConstants() {
		throw new IllegalStateException("Utility class");
	}
}