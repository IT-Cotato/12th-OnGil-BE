package com.ongil.backend.domain.brand.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BrandResponse {

	private Long id;
	private String name;
	private String description;
	private String logoImageUrl;
}
