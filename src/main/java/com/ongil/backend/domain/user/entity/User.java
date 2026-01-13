package com.ongil.backend.domain.user.entity;

import java.util.*;

import com.ongil.backend.domain.address.entity.*;
import com.ongil.backend.domain.auth.entity.LoginType;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(
	name = "users",
	indexes = { // 조회 성능 향상
		@Index(name = "idx_login_type_login_id", columnList = "login_type, login_id")
	},
	uniqueConstraints = { // 중복 가입 방지
		@UniqueConstraint(name = "uk_login_type_login_id", columnNames = {"login_type", "login_id"})
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "login_type", nullable = false)
	private LoginType loginType;

	// 일반로그인 ID 또는 소셜 고유 ID
	@Column(name = "login_id", nullable = false)
	private String loginId;

	@Column(name = "password")
	private String password;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(name = "profile_img")
	private String profileImg;

	@Column(length = 20)
	private String phone;

	private Integer height;

	private Integer weight;

	@Column(name = "usual_size", length = 10)
	private String usualSize;

	@Column(nullable = false)
	@Builder.Default
	private Integer points = 0;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Address> addresses = new ArrayList<>();
}