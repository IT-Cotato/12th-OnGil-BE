package com.ongil.backend.domain.advertisement.entity;

import com.ongil.backend.domain.brand.entity.Brand;
import com.ongil.backend.domain.category.entity.Category;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_ad_preferences",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_user_category_brand",
			columnNames = {"user_id", "preferred_category_id", "preferred_brand_id"}
		)
	})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserAdPreference extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "preference_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "preferred_category_id")
	private Category preferredCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "preferred_brand_id")
	private Brand preferredBrand;

	@Column(name = "is_interested", nullable = false)
	private Boolean isInterested = true;

	@Builder
	public UserAdPreference(User user, Category preferredCategory, Brand preferredBrand, Boolean isInterested) {
		this.user = user;
		this.preferredCategory = preferredCategory;
		this.preferredBrand = preferredBrand;
		this.isInterested = isInterested != null ? isInterested : true;
	}

	public void updateInterest(Boolean isInterested) {
		this.isInterested = isInterested;
	}
}
