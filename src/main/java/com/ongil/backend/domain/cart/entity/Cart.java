package com.ongil.backend.domain.cart.entity;

import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Cart extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_id")
	private Long id;

	@Column(name = "selected_size", length = 20)
	private String selectedSize;

	@Column(name = "selected_color", length = 50)
	private String selectedColor;

	@Column(nullable = false)
	private Integer quantity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Builder
	public Cart(String selectedSize, String selectedColor, Integer quantity,
		User user, Product product) {
		this.selectedSize = selectedSize;
		this.selectedColor = selectedColor;
		this.quantity = quantity;
		this.user = user;
		this.product = product;
	}

	public void updateSize(String selectedSize) {
		this.selectedSize = selectedSize;
	}

	public void updateColor(String selectedColor) {
		this.selectedColor = selectedColor;
	}

	public void updateQuantity(Integer quantity) {
		if (quantity < 1) {
			throw new IllegalArgumentException("수량은 최소 1개 이상이어야 합니다.");
		}
		this.quantity = quantity;
	}

}
