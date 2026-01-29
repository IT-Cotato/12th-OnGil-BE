package com.ongil.backend.domain.address.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ShippingInfoCreateReqDto(
	@NotBlank(message = "수령인 이름은 필수입니다.")
	String recipientName,

	@NotBlank(message = "기본 주소는 필수입니다.")
	String baseAddress,

	String detailAddress,

	@NotBlank(message = "우편번호는 필수입니다.")
	String postalCode,

	@NotBlank(message = "연락처는 필수입니다.")
	String phone,

	String deliveryRequest
) {
}