package com.ongil.backend.domain.pricealert.converter;

import com.ongil.backend.domain.pricealert.dto.response.PriceAlertResponse;
import com.ongil.backend.domain.pricealert.entity.PriceAlert;

public class PriceAlertConverter {

	public static PriceAlertResponse toResponse(PriceAlert priceAlert) {
		return PriceAlertResponse.builder()
			.productId(priceAlert.getProduct().getId())
			.currentPrice(priceAlert.getProduct().getPrice())
			.targetPrice(priceAlert.getTargetPrice())
			.isNotified(priceAlert.getIsNotified())
			.isActive(priceAlert.getIsActive())
			.build();
	}
}