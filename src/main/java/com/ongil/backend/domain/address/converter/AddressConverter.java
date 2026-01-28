package com.ongil.backend.domain.address.converter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.address.dto.response.AddressResponse;
import com.ongil.backend.domain.address.entity.Address;

@Component
public class AddressConverter {

	public AddressResponse toResponse(Address address) {
		return AddressResponse.builder()
			.addressId(address.getId())
			.recipientName(address.getRecipientName())
			.recipientPhone(address.getRecipientPhone())
			.baseAddress(address.getBaseAddress())
			.detailAddress(address.getDetailAddress())
			.postalCode(address.getPostalCode())
			.deliveryRequest(address.getDeliveryRequest())
			.isDefault(address.getIsDefault())
			.build();
	}

	public List<AddressResponse> toResponseList(List<Address> addresses) {
		return addresses.stream()
			.map(this::toResponse)
			.toList();
	}
}
