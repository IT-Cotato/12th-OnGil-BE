package com.ongil.backend.domain.advertisement.dto.request;

import java.time.LocalDateTime;

import com.ongil.backend.domain.advertisement.enums.AdvertisementType;

import lombok.Getter;

@Getter
public class AdvertisementUpdateRequest {
	private String title;
	private String description;
	private String imageUrl;
	private String targetUrl;
	private AdvertisementType advertisementType;
	private Integer displayOrder;
	private Long targetCategoryId;
	private Long targetBrandId;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private Boolean isActive;
}
