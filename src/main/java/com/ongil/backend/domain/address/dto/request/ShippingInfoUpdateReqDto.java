package com.ongil.backend.domain.address.dto.request;

public record ShippingInfoUpdateReqDto(
	String recipientName,
	String baseAddress,
	String detailAddress,
	String postalCode,
	String phone,
	String deliveryRequest
) {
}