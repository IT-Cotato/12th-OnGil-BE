package com.ongil.backend.domain.product.entity;

import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_options")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductOption extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_option_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(name = "size", length = 10, nullable = false)
	private String size;  // XS, S, M, L, XL

	@Column(name = "color", length = 50, nullable = false)
	private String color;  // 블랙, 베이지, 네이비 등

	@Column(name = "stock", nullable = false)
	private Integer stock = 0;  // 옵션별 재고

	@Column(name = "additional_price")
	private Integer additionalPrice = 0;  // 추가 금액 (기본 0)

	@Builder
	public ProductOption(Product product, String size, String color,
		Integer stock, Integer additionalPrice) {
		this.product = product;
		this.size = size;
		this.color = color;
		this.stock = stock != null ? stock : 0;
		this.additionalPrice = additionalPrice != null ? additionalPrice : 0;
	}

	// 재고 감소
	public void decreaseStock(int quantity) {
		if (this.stock < quantity) {
			throw new IllegalStateException(
				String.format("재고가 부족합니다. (요청: %d, 현재: %d)", quantity, this.stock)
			);
		}
		this.stock -= quantity;
	}

	// 재고 증가
	public void increaseStock(int quantity) {
		this.stock += quantity;
	}

	// 재고 있음 (1개 이상)
	public boolean isInStock() {
		return this.stock > 0;
	}

	// 재고 부족 (1~5개)
	public boolean isLowStock() {
		return this.stock > 0 && this.stock <= 5;
	}

	// 품절 (재고 0)
	public boolean isSoldOut() {
		return this.stock == 0;
	}
}
