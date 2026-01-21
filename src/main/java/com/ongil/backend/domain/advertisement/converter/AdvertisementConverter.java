package com.ongil.backend.domain.advertisement.converter;

import com.ongil.backend.domain.advertisement.dto.response.AdvertisementResponse;
import com.ongil.backend.domain.advertisement.dto.response.UserAdPreferenceResponse;
import com.ongil.backend.domain.advertisement.entity.Advertisement;
import com.ongil.backend.domain.advertisement.entity.UserAdPreference;

public class AdvertisementConverter {

	public static AdvertisementResponse toResponse(Advertisement advertisement) {
		return AdvertisementResponse.builder()
			.id(advertisement.getId())
			.title(advertisement.getTitle())
			.description(advertisement.getDescription())
			.imageUrl(advertisement.getImageUrl())
			.targetUrl(advertisement.getTargetUrl())
			.advertisementType(advertisement.getAdvertisementType())
			.displayOrder(advertisement.getDisplayOrder())
			.targetCategoryId(advertisement.getTargetCategory() != null ? advertisement.getTargetCategory().getId() : null)
			.targetCategoryName(advertisement.getTargetCategory() != null ? advertisement.getTargetCategory().getName() : null)
			.targetBrandId(advertisement.getTargetBrand() != null ? advertisement.getTargetBrand().getId() : null)
			.targetBrandName(advertisement.getTargetBrand() != null ? advertisement.getTargetBrand().getName() : null)
			.startDate(advertisement.getStartDate())
			.endDate(advertisement.getEndDate())
			.isActive(advertisement.getIsActive())
			.impressionCount(advertisement.getImpressionCount())
			.clickCount(advertisement.getClickCount())
			.build();
	}

	public static UserAdPreferenceResponse toPreferenceResponse(UserAdPreference preference) {
		return UserAdPreferenceResponse.builder()
			.id(preference.getId())
			.userId(preference.getUser().getId())
			.preferredCategoryId(preference.getPreferredCategory() != null ? preference.getPreferredCategory().getId() : null)
			.preferredCategoryName(preference.getPreferredCategory() != null ? preference.getPreferredCategory().getName() : null)
			.preferredBrandId(preference.getPreferredBrand() != null ? preference.getPreferredBrand().getId() : null)
			.preferredBrandName(preference.getPreferredBrand() != null ? preference.getPreferredBrand().getName() : null)
			.isInterested(preference.getIsInterested())
			.build();
	}
}
