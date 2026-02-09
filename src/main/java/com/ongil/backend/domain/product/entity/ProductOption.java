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
	private String size;

	@Column(name = "color", length = 50, nullable = false)
	private String color;

	@Column(name = "stock", nullable = false)
	private Integer stock = 0;

	@Builder
	public ProductOption(Product product, String size, String color, Integer stock) {
		this.product = product;
		this.size = size;
		this.color = color;
		this.stock = stock != null ? stock : 0;
	}

	// 재고 상태 반환
	public StockStatus getStockStatus() {
		return this.stock == 0 ? StockStatus.SOLD_OUT : StockStatus.AVAILABLE;
	}

	// 상품 옵션 수정
	public void updateProductOption(String size, String color, Integer stock) {
		if (size != null) {
			this.size = size;
		}
		if (color != null) {
			this.color = color;
		}
		if (stock != null) {
			// 재고 음수 방지
			if (stock < 0) {
				throw new com.ongil.backend.global.common.exception.ValidationException(
					com.ongil.backend.global.common.exception.ErrorCode.INVALID_STOCK
				);
			}
			this.stock = stock;
		}
	}

	public enum StockStatus {
		AVAILABLE,    // 구매 가능
		SOLD_OUT      // 품절
	}
}