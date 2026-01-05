package com.ongil.backend.domain.category.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.ongil.backend.domain.product.entity.Product;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_category_id")
	private Category parentCategory;

	@OneToMany(mappedBy = "parentCategory")
	private List<Category> subCategories = new ArrayList<>();

	@OneToMany(mappedBy = "category")
	private List<Product> products = new ArrayList<>();

	@Builder
	public Category(String name, Integer displayOrder, Category parentCategory) {
		this.name = name;
		this.displayOrder = displayOrder;
		this.parentCategory = parentCategory;
	}
}
