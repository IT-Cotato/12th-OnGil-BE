package com.ongil.backend.domain.address.entity;

import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Address extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "address_id")
	private Long id;

	@Column(name = "recipient_name", nullable = false, length = 50)
	private String recipientName;

	@Column(name = "recipient_phone", nullable = false, length = 20)
	private String recipientPhone;

	@Column(name = "base_address", nullable = false, length = 200)
	private String baseAddress;

	@Column(name = "detail_address", length = 200)
	private String detailAddress;

	@Column(name = "postal_code", nullable = false, length = 10)
	private String postalCode;

	@Column(name = "delivery_request", length = 200)
	private String deliveryRequest;

	@Column(name = "is_default", nullable = false)
	private Boolean isDefault = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	public Address(String recipientName, String recipientPhone, String baseAddress,
		String detailAddress, String postalCode, String deliveryRequest,
		Boolean isDefault, User user) {
		this.recipientName = recipientName;
		this.recipientPhone = recipientPhone;
		this.baseAddress = baseAddress;
		this.detailAddress = detailAddress;
		this.postalCode = postalCode;
		this.deliveryRequest = deliveryRequest;
		this.isDefault = isDefault;
		this.user = user;
	}

	// 배송지 정보 수정
	public void updateAddress(String recipientName, String recipientPhone, String baseAddress,
		String detailAddress, String postalCode, String deliveryRequest) {
		this.recipientName = recipientName;
		this.recipientPhone = recipientPhone;
		this.baseAddress = baseAddress;
		this.detailAddress = detailAddress;
		this.postalCode = postalCode;
		this.deliveryRequest = deliveryRequest;
	}

	// 기본 배송지 설정
	public void setAsDefault() {
		this.isDefault = true;
	}

	// 일반 배송지로 변경
	public void unsetDefault() {
		this.isDefault = false;
	}
}
