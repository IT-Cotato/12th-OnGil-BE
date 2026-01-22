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

	// 도움돼요 카운트
	@Column(name = "helpful_count", nullable = false)
	private Integer helpfulCount = 0;

	// 후기 내용
	@Column(name = "ai_generated_review", columnDefinition = "TEXT")
	private String aiGeneratedReview;

	@Column(name = "text_review", columnDefinition = "TEXT")
	private String textReview;

	@Column(name = "review_image_urls", columnDefinition = "TEXT")
	private String reviewImageUrls;

	// 구매 직후 리뷰 - 1차 질문
	@Column(name = "size_answer", columnDefinition = "TEXT")
	private String sizeAnswer;

	@Column(name = "color_answer", columnDefinition = "TEXT")
	private String colorAnswer;

	@Column(name = "material_answer", columnDefinition = "TEXT")
	private String materialAnswer;

	// 구매 직후 리뷰 - 2차 질문
	@Column(name = "fit_issue_parts", columnDefinition = "TEXT")
	private String fitIssueParts;

	@Column(name = "material_features", columnDefinition = "TEXT")
	private String materialFeatures;

	// 한달 후 리뷰
	@Column(name = "one_month_overall", length = 50)
	private String oneMonthOverall;  // 전체 평가

	@Column(name = "one_month_changes", columnDefinition = "TEXT")
	private String oneMonthChanges;  // 변화 항목

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
		Integer rating, String aiGeneratedReview, String textReview, String reviewImageUrls,
		String sizeAnswer, String colorAnswer, String materialAnswer,
		String fitIssueParts, String materialFeatures,
		String oneMonthOverall, String oneMonthChanges,
		User user, OrderItem orderItem, Product product) {
		this.reviewType = reviewType;
		this.reviewStatus = reviewStatus != null ? reviewStatus : ReviewStatus.DRAFT;
		this.currentStep = currentStep != null ? currentStep : 0;
		this.rating = rating;
		this.helpfulCount = 0;
		this.aiGeneratedReview = aiGeneratedReview;
		this.textReview = textReview;
		this.reviewImageUrls = reviewImageUrls;
		this.sizeAnswer = sizeAnswer;
		this.colorAnswer = colorAnswer;
		this.materialAnswer = materialAnswer;
		this.fitIssueParts = fitIssueParts;
		this.materialFeatures = materialFeatures;
		this.oneMonthOverall = oneMonthOverall;
		this.oneMonthChanges = oneMonthChanges;
		this.user = user;
		this.orderItem = orderItem;
		this.product = product;
	}

	public void incrementHelpfulCount() {
		this.helpfulCount++;
	}

	public void decrementHelpfulCount() {
		if (this.helpfulCount > 0) {
			this.helpfulCount--;
		}
	}

	public void complete(Integer points) {
		this.reviewStatus = ReviewStatus.COMPLETED;
		this.completedAt = LocalDateTime.now();
		this.earnedPoints = points;
	}
}