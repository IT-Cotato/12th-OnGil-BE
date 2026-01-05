package com.ongil.backend.domain.pricealert.entity;

import java.time.LocalDateTime;

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
	private Boolean isActive = true;

	@Column(name = "read_at")
	private LocalDateTime readAt;

	@Column(name = "is_read", nullable = false)
	private Boolean isRead = false;

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
}