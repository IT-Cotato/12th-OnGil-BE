package com.ongil.backend.domain.review.entity;

import java.time.LocalDateTime;

import com.ongil.backend.domain.order.entity.OrderItem;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.review.enums.ReviewStatus;
import com.ongil.backend.domain.review.enums.ReviewType;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "review_type", nullable = false)
	private ReviewType reviewType;

	@Enumerated(EnumType.STRING)
	@Column(name = "review_status", nullable = false)
	private ReviewStatus reviewStatus;

	@Column(name = "current_step", nullable = false)
	private Integer currentStep = 0;

	@Column(nullable = false)
	private Integer rating;

	@Column(name = "text_review", columnDefinition = "TEXT")
	private String textReview;

	@Column(name = "review_image_urls", columnDefinition = "TEXT")
	private String reviewImageUrls;

	@Column(name = "size_answer", columnDefinition = "TEXT")
	private String sizeAnswer;

	@Column(name = "color_answer", columnDefinition = "TEXT")
	private String colorAnswer;

	@Column(name = "material_answer", columnDefinition = "TEXT")
	private String materialAnswer;

	@Column(name = "length_answer", columnDefinition = "TEXT")
	private String lengthAnswer;

	@Column(name = "value_answer", columnDefinition = "TEXT")
	private String valueAnswer;

	@Column(name = "color_change", length = 50)
	private String colorChange;

	@Column(name = "washing_deformation", length = 50)
	private String washingDeformation;

	@Column(name = "material_stretching", length = 50)
	private String materialStretching;

	@Column(name = "earned_points")
	private Integer earnedPoints;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_item_id", nullable = false)
	private OrderItem orderItem;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Builder
	public Review(ReviewType reviewType, ReviewStatus reviewStatus, Integer currentStep,
		Integer rating, String textReview, String reviewImageUrls,
		String sizeAnswer, String colorAnswer, String materialAnswer,
		String lengthAnswer, String valueAnswer, String colorChange,
		String washingDeformation, String materialStretching,
		User user, OrderItem orderItem, Product product) {
		this.reviewType = reviewType;
		this.reviewStatus = reviewStatus;
		this.currentStep = currentStep;
		this.rating = rating;
		this.textReview = textReview;
		this.reviewImageUrls = reviewImageUrls;
		this.sizeAnswer = sizeAnswer;
		this.colorAnswer = colorAnswer;
		this.materialAnswer = materialAnswer;
		this.lengthAnswer = lengthAnswer;
		this.valueAnswer = valueAnswer;
		this.colorChange = colorChange;
		this.washingDeformation = washingDeformation;
		this.materialStretching = materialStretching;
		this.user = user;
		this.orderItem = orderItem;
		this.product = product;
	}
}
