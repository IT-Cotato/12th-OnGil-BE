package com.ongil.backend.domain.payment.entity;

import java.time.LocalDateTime;

import com.ongil.backend.domain.order.entity.Order;
import com.ongil.backend.domain.payment.enums.PaymentMethod;
import com.ongil.backend.domain.payment.enums.PaymentStatus;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Payment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id")
	private Long id;

	@Column(name = "payment_amount", nullable = false)
	private Integer paymentAmount;

	@Column(name = "used_points", nullable = false)
	private Integer usedPoints;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false)
	private PaymentMethod paymentMethod;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false)
	private PaymentStatus paymentStatus;

	@Column(name = "canceled_at")
	private LocalDateTime canceledAt;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false, unique = true)
	private Order order;

	@Builder
	public Payment(Integer paymentAmount, Integer usedPoints, PaymentMethod paymentMethod,
		PaymentStatus paymentStatus, Order order) {
		this.paymentAmount = paymentAmount;
		this.usedPoints = usedPoints;
		this.paymentMethod = paymentMethod;
		this.paymentStatus = paymentStatus;
		this.order = order;
	}

	public void cancelPayment() {
		this.paymentStatus = PaymentStatus.CANCELED;
		this.canceledAt = LocalDateTime.now();
	}
}
