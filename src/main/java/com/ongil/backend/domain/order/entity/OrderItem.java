package com.ongil.backend.domain.order.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.review.entity.Review;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "selected_size", length = 20)
	private String selectedSize;

	@Column(name = "selected_color", length = 50)
	private String selectedColor;

	@Column(nullable = false)
	private Integer quantity;

	@Column(name = "price_at_order", nullable = false)
	private Integer priceAtOrder;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@OneToMany(mappedBy = "orderItem")
	private List<Review> reviews = new ArrayList<>();

	@Builder
	public OrderItem(String selectedSize, String selectedColor, Integer quantity,
		Integer priceAtOrder, Order order, Product product) {
		this.selectedSize = selectedSize;
		this.selectedColor = selectedColor;
		this.quantity = quantity;
		this.priceAtOrder = priceAtOrder;
		this.order = order;
		this.product = product;
	}
}
