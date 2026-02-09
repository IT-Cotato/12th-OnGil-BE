package com.ongil.backend.domain.address.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "배송지 목록 항목")
public record AddressListResponse(

	@Schema(description = "배송지 ID")
	Long addressId,

	@Schema(description = "수령인 이름")
	String recipientName,

	@Schema(description = "수령인 연락처")
	String recipientPhone,

	@Schema(description = "기본 주소")
	String baseAddress,

	@Schema(description = "상세 주소")
	String detailAddress,

	@Schema(description = "우편번호")
	String postalCode,

	@Schema(description = "기본 배송지 여부")
	boolean isDefault
) {
}
