package com.ongil.backend.domain.brand.entity;

import java.util.*;

import com.ongil.backend.domain.product.entity.*;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "brands")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Brand extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "brand_id")
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(length = 500)
	private String description;

	@Column(name = "logo_image_url", length = 500)
	private String logoImageUrl;

	@OneToMany(mappedBy = "brand", fetch = FetchType.LAZY)
	private List<Product> products = new ArrayList<>();

	@Builder
	public Brand(String name, String description, String logoImageUrl) {
		this.name = name;
		this.description = description;
		this.logoImageUrl = logoImageUrl;
	}
}