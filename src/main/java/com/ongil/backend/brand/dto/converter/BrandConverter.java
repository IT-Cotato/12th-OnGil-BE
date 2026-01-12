package com.ongil.backend.brand.dto.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ongil.backend.brand.dto.response.BrandResponse;
import com.ongil.backend.domain.brand.entity.Brand;

@Component
public class BrandConverter {

	// 브랜드 기본 정보 변환
	public BrandResponse toResponse(Brand brand) {
		return BrandResponse.builder()
			.id(brand.getId())
			.name(brand.getName())
			.description(brand.getDescription())
			.logoImageUrl(brand.getLogoImageUrl())
			.build();
	}

	// 브랜드 리스트 변환
	public List<BrandResponse> toResponseList(List<Brand> brands) {
		return brands.stream()
			.map(this::toResponse)
			.collect(Collectors.toList());
	}
}