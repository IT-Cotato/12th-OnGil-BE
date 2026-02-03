package com.ongil.backend.domain.notification.entity;

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
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id")
	private Long id;

	@Column(name = "message", nullable = false)
	private String message;

	@Column(name = "target_url", nullable = false)
	private String targetUrl;

	@Column(name = "is_read", nullable = false)
	private Boolean isRead = false;

	@Column(name = "read_at")
	private LocalDateTime readAt;
	
	@Column(name = "notified_at", nullable = false)
	private LocalDateTime notifiedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Builder
	public Notification(String message, String targetUrl, LocalDateTime notifiedAt, User user, Product product) {
		this.message = message;
		this.targetUrl = targetUrl;
		this.notifiedAt = notifiedAt;
		this.user = user;
		this.product = product;
	}

	public void markAsRead() {
		this.isRead = true;
		this.readAt = LocalDateTime.now();
	}
}