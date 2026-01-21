package com.ongil.backend.domain.advertisement.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAdPreferenceResponse {
	private Long id;
	private Long userId;
	private Long preferredCategoryId;
	private String preferredCategoryName;
	private Long preferredBrandId;
	private String preferredBrandName;
	private Boolean isInterested;
}
