package com.ongil.backend.domain.category.entity;

import java.util.ArrayList;
import java.util.List;

import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long id;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(name = "icon_url", length = 500)
	private String iconUrl;

	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_category_id")
	private Category parentCategory;

	@OneToMany(mappedBy = "parentCategory")
	private List<Category> subCategories = new ArrayList<>();

	@Builder
	public Category(String name, String iconUrl, Integer displayOrder, Category parentCategory) {
		this.name = name;
		this.iconUrl = iconUrl;
		this.displayOrder = displayOrder;
		this.parentCategory = parentCategory;
	}

	// 카테고리 정보 수정
	public void updateCategory(String name, String iconUrl, Integer displayOrder, Category parentCategory) {
		if (name != null) {
			this.name = name;
		}
		if (iconUrl != null) {
			this.iconUrl = iconUrl;
		}
		if (displayOrder != null) {
			this.displayOrder = displayOrder;
		}
		if (parentCategory != null) {
			// 자기 참조(순환 참조) 방지
			if (parentCategory.getId().equals(this.id)) {
				throw new com.ongil.backend.global.common.exception.ValidationException(
					com.ongil.backend.global.common.exception.ErrorCode.CATEGORY_SELF_REFERENCE
				);
			}
			this.parentCategory = parentCategory;
		}
	}
}