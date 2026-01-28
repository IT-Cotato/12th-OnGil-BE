package com.ongil.backend.domain.banner.converter;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.banner.dto.response.BannerResponse;
import com.ongil.backend.domain.banner.enums.BannerType;

@Component
public class BannerConverter {

	public BannerResponse toResponse(BannerType type, String title, String buttonText,
		String targetUrl, Long targetId, boolean enabled) {
		return BannerResponse.builder()
			.type(type)
			.title(title)
			.buttonText(buttonText)
			.targetUrl(targetUrl)
			.targetId(targetId)
			.enabled(enabled)
			.build();
	}
}