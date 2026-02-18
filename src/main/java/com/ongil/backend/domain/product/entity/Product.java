package com.ongil.backend.domain.product.entity;

import org.hibernate.annotations.Formula;

import com.ongil.backend.domain.brand.entity.Brand;
import com.ongil.backend.domain.category.entity.Category;
import com.ongil.backend.domain.product.enums.ProductType;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long id;

	@Column(nullable = false, length = 200)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private Integer price;

	@Column(name = "material_original", length = 500)
	private String materialOriginal;

	// AI 소재 설명(장점, 단점, 세탁법)
	@Column(name = "ai_material_advantages", columnDefinition = "TEXT")
	private String aiMaterialAdvantages;

	@Column(name = "ai_material_disadvantages", columnDefinition = "TEXT")
	private String aiMaterialDisadvantages;

	@Column(name = "ai_material_care", columnDefinition = "TEXT")
	private String aiMaterialCare;

	@Column(name = "image_urls", columnDefinition = "TEXT")
	private String imageUrls;

	@Column(length = 100)
	private String sizes;

	@Column(length = 100)
	private String colors;

	@Column(name = "discount_rate")
	private Integer discountRate;

	@Column(name = "discount_price")
	private Integer discountPrice;

	@Column(name = "view_count", nullable = false)
	private Integer viewCount = 0;

	@Column(name = "purchase_count", nullable = false)
	private Integer purchaseCount = 0;

	@Column(name = "cart_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	private Integer cartCount = 0;

	@Column(name = "review_count")
	private Integer reviewCount = 0;

	@Column(name = "review_rating", nullable = false)
	private Double reviewRating = 0.0;

	@Formula("coalesce(view_count, 0) + coalesce(purchase_count, 0)")
	private Integer popularity;

	@Column(name = "on_sale", nullable = false)
	private Boolean onSale = true;

	@Enumerated(EnumType.STRING)
	@Column(name = "product_type", nullable = false)
	private ProductType productType = ProductType.NORMAL;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "brand_id", nullable = false)
	private Brand brand;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@Builder
	public Product(String name, String description, Integer price,
		String materialOriginal,
		String aiMaterialAdvantages,
		String aiMaterialDisadvantages,
		String aiMaterialCare,
		String imageUrls,
		String sizes, String colors, Integer discountRate,
		Integer discountPrice, ProductType productType,
		Brand brand, Category category) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.materialOriginal = materialOriginal;
		this.aiMaterialAdvantages = aiMaterialAdvantages;
		this.aiMaterialDisadvantages = aiMaterialDisadvantages;
		this.aiMaterialCare = aiMaterialCare;
		this.imageUrls = imageUrls;
		this.sizes = sizes;
		this.colors = colors;
		this.discountRate = discountRate;
		this.discountPrice = discountPrice;
		this.productType = productType;
		this.brand = brand;
		this.category = category;
	}

	// AI 소재 설명 업데이트
	public void updateAiMaterialDescription(
		String advantages,
		String disadvantages,
		String care
	) {
		this.aiMaterialAdvantages = advantages;
		this.aiMaterialDisadvantages = disadvantages;
		this.aiMaterialCare = care;
	}

	// 실제 가격 반환 (할인가가 있으면 할인가, 없으면 원가)
	public Integer getEffectivePrice() {
		return (discountPrice != null && discountPrice > 0) ? discountPrice : price;
	}

	// 상품 정보 수정
	public void updateProduct(String name, String description, Integer price, String materialOriginal,
		String imageUrls, String sizes, String colors, Integer discountRate, ProductType productType,
		Brand brand, Category category) {
		if (name != null) {
			this.name = name;
		}
		if (description != null) {
			this.description = description;
		}
		if (price != null) {
			this.price = price;
			// 가격 변경 시 할인가 재계산
			if (this.discountRate != null && this.discountRate > 0) {
				this.discountPrice = price - (price * this.discountRate / 100);
			}
		}
		if (materialOriginal != null) {
			this.materialOriginal = materialOriginal;
		}
		if (imageUrls != null) {
			this.imageUrls = imageUrls;
		}
		if (sizes != null) {
			this.sizes = sizes;
		}
		if (colors != null) {
			this.colors = colors;
		}
		if (discountRate != null) {
			this.discountRate = discountRate;
			// 할인율 변경 시 할인가 재계산
			if (this.price != null && discountRate > 0) {
				this.discountPrice = this.price - (this.price * discountRate / 100);
			} else {
				this.discountPrice = null;
			}
		}
		if (productType != null) {
			this.productType = productType;
		}
		if (brand != null) {
			this.brand = brand;
		}
		if (category != null) {
			this.category = category;
		}
	}

	// 리뷰 개수 및 평점 업데이트
	public void updateReviewStats(Integer count, Double rating) {
		this.reviewCount = count;
		this.reviewRating = rating;
	}
}