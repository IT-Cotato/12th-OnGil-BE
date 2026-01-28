package com.ongil.backend.domain.address.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "배송지 응답")
public class AddressResponse {

	@Schema(description = "배송지 ID")
	private Long addressId;

	@Schema(description = "수령인 이름")
	private String recipientName;

	@Schema(description = "수령인 전화번호")
	private String recipientPhone;

	@Schema(description = "기본 주소")
	private String baseAddress;

	@Schema(description = "상세 주소")
	private String detailAddress;

	@Schema(description = "우편번호")
	private String postalCode;

	@Schema(description = "배송 요청사항")
	private String deliveryRequest;

	@Schema(description = "기본 배송지 여부")
	private Boolean isDefault;
}
