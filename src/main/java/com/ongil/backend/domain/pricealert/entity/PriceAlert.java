package com.ongil.backend.domain.pricealert.entity;

import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "price_alerts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PriceAlert extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "price_alert_id")
	private Long id;

	@Column(name = "target_price", nullable = false)
	private Integer targetPrice;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true; // 알림 활성화 여부

	@Column(name = "is_notified", nullable = false)
	private Boolean isNotified = false; // 알림 발송 여부

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Builder
	public PriceAlert(Integer targetPrice, Boolean isActive, User user, Product product) {
		this.targetPrice = targetPrice;
		this.isActive = isActive;
		this.user = user;
		this.product = product;
	}

	public void markAsNotified() { // 가격 알림이 발송되었음을 표시
		this.isNotified = true;
	}

	public void deactivate() { // 알림을 비활성화
		this.isActive = false;
	}
}