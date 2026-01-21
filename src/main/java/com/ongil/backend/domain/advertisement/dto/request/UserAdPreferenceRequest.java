package com.ongil.backend.domain.advertisement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserAdPreferenceRequest {
	private Long preferredCategoryId;
	private Long preferredBrandId;

	@NotNull(message = "관심 여부는 필수입니다")
	private Boolean isInterested;
}
