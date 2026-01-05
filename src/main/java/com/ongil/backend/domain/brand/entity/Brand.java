package com.ongil.backend.domain.brand.entity;

import java.time.*;
import java.util.*;

import org.hibernate.annotations.*;

import com.ongil.backend.domain.product.entity.*;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "brands")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Brand {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(length = 500)
	private String description;

	@Column(name = "logo_image_url", length = 500)
	private String logoImageUrl;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "brand")
	private List<Product> products = new ArrayList<>();

	@Builder
	public Brand(String name, String description, String logoImageUrl) {
		this.name = name;
		this.description = description;
		this.logoImageUrl = logoImageUrl;
	}
}