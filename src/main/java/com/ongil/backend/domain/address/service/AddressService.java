package com.ongil.backend.domain.address.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.address.converter.AddressConverter;
import com.ongil.backend.domain.address.dto.request.AddressCreateRequest;
import com.ongil.backend.domain.address.dto.request.AddressUpdateRequest;
import com.ongil.backend.domain.address.dto.response.AddressResponse;
import com.ongil.backend.domain.address.entity.Address;
import com.ongil.backend.domain.address.repository.AddressRepository;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

	private final AddressRepository addressRepository;
	private final UserRepository userRepository;
	private final AddressConverter addressConverter;

	// 내 배송지 목록 조회
	public List<AddressResponse> getMyAddresses(Long userId) {
		List<Address> addresses = addressRepository.findByUserIdOrderByCreatedAtDesc(userId);
		return addressConverter.toResponseList(addresses);
	}

	// 특정 배송지 조회
	public AddressResponse getAddress(Long userId, Long addressId) {
		Address address = addressRepository.findByIdAndUserId(addressId, userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.ADDRESS_NOT_FOUND));
		return addressConverter.toResponse(address);
	}

	// 기본 배송지 조회
	public AddressResponse getDefaultAddress(Long userId) {
		Address address = addressRepository.findByUserIdAndIsDefaultTrue(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.ADDRESS_NOT_FOUND));
		return addressConverter.toResponse(address);
	}

	// 배송지 추가
	@Transactional
	public AddressResponse createAddress(Long userId, AddressCreateRequest request) {
		// 사용자 존재 여부 확인
		if (!userRepository.existsById(userId)) {
			throw new EntityNotFoundException(ErrorCode.USER_NOT_FOUND);
		}

		// 기본 배송지로 설정하는 경우, 기존 기본 배송지를 일반 배송지로 변경
		if (Boolean.TRUE.equals(request.isDefault())) {
			addressRepository.unsetDefaultAddress(userId);
		}

		// 배송지 생성
		Address address = Address.builder()
			.user(User.builder().id(userId).build())
			.recipientName(request.recipientName())
			.recipientPhone(request.recipientPhone())
			.baseAddress(request.baseAddress())
			.detailAddress(request.detailAddress())
			.postalCode(request.postalCode())
			.deliveryRequest(request.deliveryRequest())
			.isDefault(request.isDefault() != null ? request.isDefault() : false)
			.build();

		Address savedAddress = addressRepository.save(address);
		return addressConverter.toResponse(savedAddress);
	}

	// 배송지 수정
	@Transactional
	public AddressResponse updateAddress(Long userId, Long addressId, AddressUpdateRequest request) {
		Address address = addressRepository.findByIdAndUserId(addressId, userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.ADDRESS_NOT_FOUND));

		// 배송지 정보 수정
		address.updateAddress(
			request.recipientName(),
			request.recipientPhone(),
			request.baseAddress(),
			request.detailAddress(),
			request.postalCode(),
			request.deliveryRequest()
		);

		return addressConverter.toResponse(address);
	}

	// 기본 배송지 설정
	@Transactional
	public AddressResponse setDefaultAddress(Long userId, Long addressId) {
		Address address = addressRepository.findByIdAndUserId(addressId, userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.ADDRESS_NOT_FOUND));

		// 기존 기본 배송지를 일반 배송지로 변경
		addressRepository.unsetDefaultAddress(userId);

		// 선택한 배송지를 기본 배송지로 설정
		address.setAsDefault();

		return addressConverter.toResponse(address);
	}

	// 배송지 삭제
	@Transactional
	public void deleteAddress(Long userId, Long addressId) {
		int deleted = addressRepository.deleteByIdAndUserId(addressId, userId);

		if (deleted == 0) {
			throw new EntityNotFoundException(ErrorCode.ADDRESS_NOT_FOUND);
		}
	}
}
