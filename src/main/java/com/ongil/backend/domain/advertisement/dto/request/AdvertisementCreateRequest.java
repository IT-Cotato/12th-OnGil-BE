package com.ongil.backend.domain.advertisement.dto.request;

import java.time.LocalDateTime;

import com.ongil.backend.domain.advertisement.enums.AdvertisementType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AdvertisementCreateRequest {
	@NotBlank(message = "제목은 필수입니다")
	private String title;

	private String description;

	@NotBlank(message = "이미지 URL은 필수입니다")
	private String imageUrl;

	private String targetUrl;

	@NotNull(message = "광고 타입은 필수입니다")
	private AdvertisementType advertisementType;

	@NotNull(message = "표시 순서는 필수입니다")
	private Integer displayOrder;

	private Long targetCategoryId;
	private Long targetBrandId;

	@NotNull(message = "시작 날짜는 필수입니다")
	private LocalDateTime startDate;

	@NotNull(message = "종료 날짜는 필수입니다")
	private LocalDateTime endDate;

	private Boolean isActive = true;
}
