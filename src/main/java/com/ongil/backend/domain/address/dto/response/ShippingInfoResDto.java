package com.ongil.backend.domain.address.dto.response;

import lombok.Builder;

@Builder
public record ShippingInfoResDto(
	boolean hasShippingInfo,
	ShippingDetail shippingDetail
) {

	@Builder
	public record ShippingDetail(
		Long addressId,
		String recipientName,
		String address,
		String postalCode,
		String phone,
		String deliveryRequest
	) {
	}
}