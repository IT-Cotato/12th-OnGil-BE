package com.ongil.backend.global.config.redis;

public class CacheKeyConstants {

	// Brand/Category 캐시 관련
	public static final String BRANDS_ALL = "BRANDS:ALL";
	public static final String CATEGORIES_ALL = "CATEGORIES:ALL";
	public static final String TODAY_RECOMMENDED_CATEGORIES = "CATEGORIES:TODAY_RECOMMENDED";
	public static final long MASTER_DATA_TTL_HOURS = 0;  // 무한 TTL
	public static final long DAILY_TTL_HOURS = 24;  // 하루 TTL

	private CacheKeyConstants() {
		throw new IllegalStateException("Utility class");
	}
}