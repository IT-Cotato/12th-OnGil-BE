package com.ongil.backend.domain.user.entity;

import java.time.*;
import java.util.*;

import org.hibernate.annotations.*;

import com.ongil.backend.domain.address.entity.*;
import com.ongil.backend.domain.cart.entity.*;
import com.ongil.backend.domain.magazine.entity.*;
import com.ongil.backend.domain.order.entity.*;
import com.ongil.backend.domain.pricealert.entity.*;
import com.ongil.backend.domain.review.entity.*;
import com.ongil.backend.domain.user.enums.*;
import com.ongil.backend.domain.wishlist.entity.*;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(length = 20)
	private String phone;

	@Column(name = "social_provider", length = 20)
	private String socialProvider;

	@Column(name = "social_id", length = 100)
	private String socialId;

	private Integer height;

	private Integer weight;

	@Column(name = "usual_size", length = 10)
	private String usualSize;

	@Column(nullable = false)
	private Integer points = 0;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role = UserRole.USER;

	@CreationTimestamp
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "user")
	private List<Address> addresses = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<PriceAlert> priceAlerts = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<MagazineComment> magazineComments = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Review> reviews = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Wishlist> wishlists = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Cart> carts = new ArrayList<>();

	@OneToMany(mappedBy = "user")
	private List<Order> orders = new ArrayList<>();

	@Builder
	public User(String email, String name, String phone, String socialProvider,
		String socialId, Integer height, Integer weight, String usualSize,
		UserRole role) {
		this.email = email;
		this.name = name;
		this.phone = phone;
		this.socialProvider = socialProvider;
		this.socialId = socialId;
		this.height = height;
		this.weight = weight;
		this.usualSize = usualSize;
		this.role = role;
	}
}