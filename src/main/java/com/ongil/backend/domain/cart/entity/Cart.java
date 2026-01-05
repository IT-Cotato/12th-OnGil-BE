package com.ongil.backend.domain.cart.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.user.entity.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "selected_size", length = 20)
	private String selectedSize;

	@Column(name = "selected_color", length = 50)
	private String selectedColor;

	@Column(nullable = false)
	private Integer quantity;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

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
}
