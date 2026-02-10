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

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {

	private final AddressRepository addressRepository;
	private final UserRepository userRepository;
	private final EntityManager entityManager;

	public List<AddressListResponse> getAddressList(Long userId) {
		List<Address> addresses = addressRepository.findAllByUserIdOrderByIsDefaultDescCreatedAtDesc(userId);

		return addresses.stream()
			.map(address -> new AddressListResponse(
				address.getId(),
				address.getRecipientName(),
				address.getRecipientPhone(),
				address.getBaseAddress(),
				address.getDetailAddress(),
				address.getPostalCode(),
				address.isDefault()
			))
			.toList();
	}

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

		// 기존 배송지 존재 여부 확인 (최적화: 전체 조회 대신 존재 여부만 확인)
		boolean hasExistingAddresses = addressRepository.existsByUserId(userId);

		// 새 배송지 등록 (첫 번째 주소는 자동으로 기본 배송지)
		Address address = AddressConverter.toEntity(user, request, !hasExistingAddresses);
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
	public void setDefaultAddress(Long userId, Long addressId) {
		Address address = addressRepository.findById(addressId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.ADDRESS_NOT_FOUND));

		if (!address.getUser().getId().equals(userId)) {
			throw new ForbiddenException(ErrorCode.ADDRESS_FORBIDDEN);
		}

		// 기존 기본 배송지 해제 (더 효율적으로 단일 조회)
		Optional<Address> currentDefaultAddress = addressRepository.findByUserIdAndIsDefaultTrue(userId);
		currentDefaultAddress.ifPresent(addr -> {
			if (!addr.getId().equals(addressId)) {
				addr.setDefault(false);
			}
		});

		// 새로운 기본 배송지 설정
		address.setDefault(true);
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
		
		// 삭제를 즉시 DB에 반영하여 다음 조회에서 제외되도록 함
		entityManager.flush();

		// 삭제한 주소가 기본 배송지였다면, 남은 주소 중 가장 최근 주소를 기본으로 설정
		if (wasDefault) {
			Optional<Address> nextAddress = addressRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
			nextAddress.ifPresent(addr -> addr.setDefault(true));
		}
	}
}