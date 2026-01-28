package com.ongil.backend.domain.address.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.address.converter.AddressConverter;
import com.ongil.backend.domain.address.dto.request.ShippingInfoCreateReqDto;
import com.ongil.backend.domain.address.dto.request.ShippingInfoUpdateReqDto;
import com.ongil.backend.domain.address.dto.response.ShippingInfoResDto;
import com.ongil.backend.domain.address.entity.Address;
import com.ongil.backend.domain.address.repository.AddressRepository;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.common.exception.ForbiddenException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

	private final AddressRepository addressRepository;
	private final UserRepository userRepository;

	public ShippingInfoResDto getShippingInfo(Long userId) {
		Optional<Address> address = addressRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);

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

		// 기존 배송지 벌크 삭제
		addressRepository.deleteAllByUserId(userId);

		// 새 배송지 등록
		Address address = AddressConverter.toEntity(user, request);
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
}