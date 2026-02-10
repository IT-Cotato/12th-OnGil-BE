package com.ongil.backend.domain.address.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.address.converter.AddressConverter;
import com.ongil.backend.domain.address.dto.request.ShippingInfoCreateReqDto;
import com.ongil.backend.domain.address.dto.request.ShippingInfoUpdateReqDto;
import com.ongil.backend.domain.address.dto.response.AddressListResponse;
import com.ongil.backend.domain.address.dto.response.ShippingInfoResDto;
import com.ongil.backend.domain.address.entity.Address;
import com.ongil.backend.domain.address.repository.AddressRepository;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.common.exception.ForbiddenException;
import com.ongil.backend.global.common.exception.ValidationException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

	private static final int MAX_ADDRESS_COUNT = 5;

	private final AddressRepository addressRepository;
	private final UserRepository userRepository;

	public List<AddressListResponse> getAddressList(Long userId) {
		List<Address> addresses = addressRepository.findAllByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);

		return addresses.stream()
			.map(AddressConverter::toAddressListResponse)
			.toList();
	}

	public ShippingInfoResDto getShippingInfo(Long userId) {
		Optional<Address> address = addressRepository.findFirstByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);

		if (address.isEmpty()) {
			return ShippingInfoResDto.builder()
				.hasShippingInfo(false)
				.shippingDetail(null)
				.build();
		}

		return AddressConverter.toShippingInfoResDto(address.get());
	}

	@Transactional
	public ShippingInfoResDto createShippingInfo(Long userId, ShippingInfoCreateReqDto request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

		long count = addressRepository.countByUserId(userId);
		if (count >= MAX_ADDRESS_COUNT) {
			throw new ValidationException(ErrorCode.ADDRESS_LIMIT_EXCEEDED);
		}

		boolean isDefault = (count == 0);
		Address address = AddressConverter.toEntity(user, request, isDefault);
		Address savedAddress = addressRepository.save(address);

		return AddressConverter.toShippingInfoResDto(savedAddress);
	}

	@Transactional
	public ShippingInfoResDto updateShippingInfo(Long userId, Long addressId, ShippingInfoUpdateReqDto request) {
		Address address = addressRepository.findById(addressId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.ADDRESS_NOT_FOUND));

		if (!address.getUser().getId().equals(userId)) {
			throw new ForbiddenException(ErrorCode.ADDRESS_FORBIDDEN);
		}

		address.update(
			request.recipientName(),
			request.phone(),
			request.baseAddress(),
			request.detailAddress(),
			request.postalCode(),
			request.deliveryRequest()
		);

		return AddressConverter.toShippingInfoResDto(address);
	}

	@Transactional
	public void deleteAddress(Long userId, Long addressId) {
		Address address = addressRepository.findById(addressId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.ADDRESS_NOT_FOUND));

		if (!address.getUser().getId().equals(userId)) {
			throw new ForbiddenException(ErrorCode.ADDRESS_FORBIDDEN);
		}

		boolean wasDefault = address.isDefault();
		addressRepository.delete(address);

		if (wasDefault) {
			addressRepository.findFirstByUserIdOrderByIsDefaultDescCreatedAtDesc(userId)
				.ifPresent(next -> next.setDefault(true));
		}
	}

	@Transactional
	public void setDefaultAddress(Long userId, Long addressId) {
		Address address = addressRepository.findById(addressId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.ADDRESS_NOT_FOUND));

		if (!address.getUser().getId().equals(userId)) {
			throw new ForbiddenException(ErrorCode.ADDRESS_FORBIDDEN);
		}

		addressRepository.findFirstByUserIdOrderByIsDefaultDescCreatedAtDesc(userId)
			.filter(Address::isDefault)
			.ifPresent(current -> current.setDefault(false));

		address.setDefault(true);
	}
}
