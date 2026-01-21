package com.ongil.backend.domain.advertisement.dto.response;

import java.time.LocalDateTime;

import com.ongil.backend.domain.advertisement.enums.AdvertisementType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdvertisementResponse {
	private Long id;
	private String title;
	private String description;
	private String imageUrl;
	private String targetUrl;
	private AdvertisementType advertisementType;
	private Integer displayOrder;
	private Long targetCategoryId;
	private String targetCategoryName;
	private Long targetBrandId;
	private String targetBrandName;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private Boolean isActive;
	private Integer impressionCount;
	private Integer clickCount;
}
