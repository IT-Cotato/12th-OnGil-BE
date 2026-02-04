package com.ongil.backend.domain.product.entity;

import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_view_histories",
	indexes = {
		@Index(name = "idx_view_history_user_created", columnList = "user_id, created_at"),
		@Index(name = "idx_view_history_product", columnList = "product_id")
	})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductViewHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "view_history_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Builder
	public ProductViewHistory(User user, Product product) {
		this.user = user;
		this.product = product;
	}
}
