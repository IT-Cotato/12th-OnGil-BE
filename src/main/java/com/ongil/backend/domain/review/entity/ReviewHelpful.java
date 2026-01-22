package com.ongil.backend.domain.review.entity;

import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "review_helpful",
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_review_helpful_review_user",
			columnNames = {"review_id", "user_id"}
		)
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReviewHelpful extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_helpful_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id", nullable = false)
	private Review review;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Builder
	public ReviewHelpful(Review review, User user) {
		this.review = review;
		this.user = user;
	}
}

