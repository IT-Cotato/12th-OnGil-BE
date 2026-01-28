package com.ongil.backend.domain.banner.dto.response;

import com.ongil.backend.domain.banner.enums.BannerType;

import lombok.Builder;

@Builder
public record BannerResponse(
	BannerType type,
	String title,
	String buttonText,
	String targetUrl,
	Long targetId,
	boolean enabled
) {
}