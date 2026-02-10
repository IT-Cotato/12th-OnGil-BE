package com.ongil.backend.domain.address.converter;

import com.ongil.backend.domain.address.dto.request.ShippingInfoCreateReqDto;
import com.ongil.backend.domain.address.dto.response.AddressListResponse;
import com.ongil.backend.domain.address.dto.response.ShippingInfoResDto;
import com.ongil.backend.domain.address.entity.Address;
import com.ongil.backend.domain.user.entity.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AddressConverter {

	public static ShippingInfoResDto toShippingInfoResDto(Address address) {
		if (address == null) {
			return ShippingInfoResDto.builder()
				.hasShippingInfo(false)
				.shippingDetail(null)
				.build();
		}

		String fullAddress = buildFullAddress(address.getBaseAddress(), address.getDetailAddress());

		ShippingInfoResDto.ShippingDetail detail = ShippingInfoResDto.ShippingDetail.builder()
			.addressId(address.getId())
			.recipientName(address.getRecipientName())
			.address(fullAddress)
			.postalCode(address.getPostalCode())
			.phone(address.getRecipientPhone())
			.deliveryRequest(address.getDeliveryRequest())
			.build();

		return ShippingInfoResDto.builder()
			.hasShippingInfo(true)
			.shippingDetail(detail)
			.build();
	}

	public static AddressListResponse toAddressListResponse(Address address) {
		return new AddressListResponse(
			address.getId(),
			address.getRecipientName(),
			address.getRecipientPhone(),
			address.getBaseAddress(),
			address.getDetailAddress(),
			address.getPostalCode(),
			address.isDefault()
		);
	}

	public static Address toEntity(User user, ShippingInfoCreateReqDto request, boolean isDefault) {
		return Address.builder()
			.user(user)
			.recipientName(request.recipientName())
			.recipientPhone(request.phone())
			.baseAddress(request.baseAddress())
			.detailAddress(request.detailAddress())
			.postalCode(request.postalCode())
			.deliveryRequest(request.deliveryRequest())
			.isDefault(isDefault)
			.build();
	}

	private static String buildFullAddress(String baseAddress, String detailAddress) {
		if (detailAddress == null || detailAddress.isBlank()) {
			return baseAddress;
		}
		return baseAddress + " " + detailAddress;
	}
}