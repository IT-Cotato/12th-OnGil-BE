package com.ongil.backend.domain.product.entity;

import java.time.*;
import java.util.*;

import org.hibernate.annotations.*;

import com.ongil.backend.domain.brand.entity.*;
import com.ongil.backend.domain.cart.entity.*;
import com.ongil.backend.domain.category.entity.*;
import com.ongil.backend.domain.product.enums.*;
import com.ongil.backend.domain.review.entity.*;
import com.ongil.backend.domain.wishlist.entity.*;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = false)
	private Integer price;

	@Column(name = "material_original", length = 500)
	private String materialOriginal;

	@Column(name = "ai_material_description", columnDefinition = "TEXT")
	private String aiMaterialDescription;

	@Column(name = "washing_method", length = 500)
	private String washingMethod;

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

	@Column(name = "on_sale", nullable = false)
	private Boolean onSale = true;

	@Enumerated(EnumType.STRING)
	@Column(name = "product_type", nullable = false)
	private ProductType productType = ProductType.NORMAL;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "brand_id", nullable = false)
	private Brand brand;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@OneToMany(mappedBy = "product")
	private List<Review> reviews = new ArrayList<>();

	@OneToMany(mappedBy = "product")
	private List<Wishlist> wishlists = new ArrayList<>();

	@OneToMany(mappedBy = "product")
	private List<Cart> carts = new ArrayList<>();

	@Builder
	public Product(String name, String description, Integer price, String materialOriginal,
		String aiMaterialDescription, String washingMethod, String imageUrls,
		String sizes, String colors, Integer discountRate, Integer discountPrice,
		ProductType productType, Brand brand, Category category) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.materialOriginal = materialOriginal;
		this.aiMaterialDescription = aiMaterialDescription;
		this.washingMethod = washingMethod;
		this.imageUrls = imageUrls;
		this.sizes = sizes;
		this.colors = colors;
		this.discountRate = discountRate;
		this.discountPrice = discountPrice;
		this.productType = productType;
		this.brand = brand;
		this.category = category;
	}
}