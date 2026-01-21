package com.ongil.backend.domain.advertisement.entity;

import java.time.LocalDateTime;

import com.ongil.backend.domain.advertisement.enums.AdvertisementType;
import com.ongil.backend.domain.brand.entity.Brand;
import com.ongil.backend.domain.category.entity.Category;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "advertisements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Advertisement extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "advertisement_id")
	private Long id;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "image_url", nullable = false, length = 500)
	private String imageUrl;

	@Column(name = "target_url", length = 500)
	private String targetUrl;

	@Enumerated(EnumType.STRING)
	@Column(name = "advertisement_type", nullable = false)
	private AdvertisementType advertisementType;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_category_id")
	private Category targetCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "target_brand_id")
	private Brand targetBrand;

	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;

	@Column(name = "end_date", nullable = false)
	private LocalDateTime endDate;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	@Column(name = "impression_count", nullable = false)
	private Integer impressionCount = 0;

	@Column(name = "click_count", nullable = false)
	private Integer clickCount = 0;

	@Builder
	public Advertisement(String title, String description, String imageUrl, String targetUrl,
		AdvertisementType advertisementType, Integer displayOrder,
		Category targetCategory, Brand targetBrand,
		LocalDateTime startDate, LocalDateTime endDate, Boolean isActive) {
		this.title = title;
		this.description = description;
		this.imageUrl = imageUrl;
		this.targetUrl = targetUrl;
		this.advertisementType = advertisementType;
		this.displayOrder = displayOrder;
		this.targetCategory = targetCategory;
		this.targetBrand = targetBrand;
		this.startDate = startDate;
		this.endDate = endDate;
		this.isActive = isActive != null ? isActive : true;
	}

	public void incrementImpressionCount() {
		this.impressionCount++;
	}

	public void incrementClickCount() {
		this.clickCount++;
	}

	public void updateStatus(Boolean isActive) {
		this.isActive = isActive;
	}
}
