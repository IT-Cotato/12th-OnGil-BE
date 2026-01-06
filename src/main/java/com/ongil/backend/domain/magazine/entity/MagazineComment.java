package com.ongil.backend.domain.magazine.entity;

import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "magazine_comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MagazineComment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 500)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "magazine_id", nullable = false)
	private Magazine magazine;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	public MagazineComment(String content, Magazine magazine, User user) {
		this.content = content;
		this.magazine = magazine;
		this.user = user;
	}
}

