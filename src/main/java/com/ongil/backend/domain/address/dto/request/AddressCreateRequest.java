package com.ongil.backend.domain.address.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "배송지 생성 요청")
public record AddressCreateRequest(

	@Schema(description = "수령인 이름", example = "홍길동")
	@NotBlank(message = "수령인 이름은 필수입니다")
	@Size(max = 50, message = "수령인 이름은 50자를 초과할 수 없습니다")
	String recipientName,

	@Schema(description = "수령인 전화번호", example = "010-1234-5678")
	@NotBlank(message = "수령인 전화번호는 필수입니다")
	@Size(max = 20, message = "전화번호는 20자를 초과할 수 없습니다")
	String recipientPhone,

	@Schema(description = "기본 주소", example = "서울특별시 강남구 테헤란로 123")
	@NotBlank(message = "기본 주소는 필수입니다")
	@Size(max = 200, message = "기본 주소는 200자를 초과할 수 없습니다")
	String baseAddress,

	@Schema(description = "상세 주소", example = "101동 202호")
	@Size(max = 200, message = "상세 주소는 200자를 초과할 수 없습니다")
	String detailAddress,

	@Schema(description = "우편번호", example = "06234")
	@NotBlank(message = "우편번호는 필수입니다")
	@Size(max = 10, message = "우편번호는 10자를 초과할 수 없습니다")
	String postalCode,

	@Schema(description = "배송 요청사항", example = "문 앞에 놓아주세요")
	@Size(max = 200, message = "배송 요청사항은 200자를 초과할 수 없습니다")
	String deliveryRequest,

	@Schema(description = "기본 배송지 여부", example = "false")
	Boolean isDefault
) {
}
