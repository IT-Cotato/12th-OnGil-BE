package com.ongil.backend.domain.address.dto.request;

import com.ongil.backend.global.common.validation.NullOrNotBlank;

public record ShippingInfoUpdateReqDto(
	@NullOrNotBlank(message = "수령인 이름은 비어있을 수 없습니다.")
	String recipientName,

	@NullOrNotBlank(message = "기본 주소는 비어있을 수 없습니다.")
	String baseAddress,

	String detailAddress,

	@NullOrNotBlank(message = "우편번호는 비어있을 수 없습니다.")
	String postalCode,

	@NullOrNotBlank(message = "연락처는 비어있을 수 없습니다.")
	String phone,

	String deliveryRequest
) {
}