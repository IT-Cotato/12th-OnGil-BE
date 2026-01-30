package com.ongil.backend.domain.magazine.converter;

import com.ongil.backend.domain.magazine.dto.response.MagazineResDto;
import com.ongil.backend.domain.magazine.entity.Magazine;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MagazineConverter {
	public static MagazineResDto from(Magazine magazine) {
		return MagazineResDto.builder()
			.id(magazine.getId())
			.title(magazine.getTitle())
			.content(magazine.getContent())
			.category(magazine.getCategory().name())
			.thumbnailUrl(magazine.getThumbnailImageUrl())
			.originalUrl(magazine.getUrl())
			.press(magazine.getPress())
			.publishedAt(magazine.getPublishedAt().toString())
			.build();
	}
}
