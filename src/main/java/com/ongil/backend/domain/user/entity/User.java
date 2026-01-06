package com.ongil.backend.domain.user.entity;

import java.util.*;

import com.ongil.backend.domain.address.entity.*;
import com.ongil.backend.domain.user.enums.*;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(name = "profile_img")
	private String profileImg;

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

	@OneToMany(mappedBy = "user")
	private List<Address> addresses = new ArrayList<>();

	@Builder
	public User(String email, String name, String profileImg, String phone, String socialProvider,
		String socialId, Integer height, Integer weight, String usualSize,
		UserRole role) {
		this.email = email;
		this.name = name;
		this.profileImg = profileImg;
		this.phone = phone;
		this.socialProvider = socialProvider;
		this.socialId = socialId;
		this.height = height;
		this.weight = weight;
		this.usualSize = usualSize;
		this.role = role;
	}
}