package com.ongil.backend.domain.order.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.ongil.backend.domain.order.enums.OrderStatus;
import com.ongil.backend.domain.payment.entity.Payment;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	@Column(name = "order_number", nullable = false, unique = true, length = 50)
	private String orderNumber;

	@Column(name = "total_amount", nullable = false)
	private Integer totalAmount;

	@Column(nullable = false, length = 50)
	private String recipient;

	@Column(name = "recipient_phone", nullable = false, length = 20)
	private String recipientPhone;

	@Column(name = "delivery_address", nullable = false, length = 200)
	private String deliveryAddress;

	@Column(name = "detail_address", length = 200)
	private String detailAddress;

	@Column(name = "postal_code", length = 10)
	private String postalCode;

	@Column(name = "delivery_message", length = 200)
	private String deliveryMessage;

	@Enumerated(EnumType.STRING)
	@Column(name = "order_status", nullable = false)
	private OrderStatus orderStatus;

	@Column(name = "shipping_started_at")
	private LocalDateTime shippingStartedAt;

	@Column(name = "delivered_at")
	private LocalDateTime deliveredAt;

	@Column(name = "confirmed_at")
	private LocalDateTime confirmedAt;

	@Column(name = "canceled_at")
	private LocalDateTime canceledAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(mappedBy = "order")
	private List<OrderItem> orderItems = new ArrayList<>();

	@OneToOne(mappedBy = "order")
	private Payment payment;

	@Builder
	public Order(String orderNumber, Integer totalAmount, String recipient,
		String recipientPhone, String deliveryAddress, String detailAddress,
		String postalCode, String deliveryMessage, OrderStatus orderStatus,
		User user) {
		this.orderNumber = orderNumber;
		this.totalAmount = totalAmount;
		this.recipient = recipient;
		this.recipientPhone = recipientPhone;
		this.deliveryAddress = deliveryAddress;
		this.detailAddress = detailAddress;
		this.postalCode = postalCode;
		this.deliveryMessage = deliveryMessage;
		this.orderStatus = orderStatus;
		this.user = user;
	}
}
