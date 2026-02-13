package com.ongil.backend.domain.review.entity;

import java.time.LocalDateTime;

import com.ongil.backend.domain.order.entity.OrderItem;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.review.enums.ClothingCategory;
import com.ongil.backend.domain.review.enums.ColorAnswer;
import com.ongil.backend.domain.review.enums.MaterialAnswer;
import com.ongil.backend.domain.review.enums.ReviewStatus;
import com.ongil.backend.domain.review.enums.ReviewType;
import com.ongil.backend.domain.review.enums.SizeAnswer;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.global.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@Builder
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
	@Builder.Default
	private Integer currentStep = 1;

	@Column(nullable = false)
	@Builder.Default
	private Integer rating = 0;

	// 도움돼요 카운트
	@Column(name = "helpful_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	@Builder.Default
	private Integer helpfulCount = 0;

	@Enumerated(EnumType.STRING)
	@Column(name = "clothing_category", nullable = false)
	private ClothingCategory clothingCategory;

	// 후기 내용
	@Column(name = "size_review", columnDefinition = "TEXT")
	private String sizeReview;

	@Column(name = "material_review", columnDefinition = "TEXT")
	private String materialReview;

	@Column(name = "text_review", columnDefinition = "TEXT")
	private String textReview;

	@Column(name = "review_image_urls", columnDefinition = "TEXT")
	private String reviewImageUrls;

	// 구매 직후 리뷰 - 1차 질문
	@Enumerated(EnumType.STRING)
	@Column(name = "size_answer")
	private SizeAnswer sizeAnswer;

	@Enumerated(EnumType.STRING)
	@Column(name = "color_answer")
	private ColorAnswer colorAnswer;

	@Enumerated(EnumType.STRING)
	@Column(name = "material_answer")
	private MaterialAnswer materialAnswer;

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
	@Builder.Default
	private Integer earnedPoints = 0;

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

	public void incrementHelpfulCount() {
		this.helpfulCount++;
	}

	public void decrementHelpfulCount() {
		if (this.helpfulCount > 0) {
			this.helpfulCount--;
		}
	}

	public void updateStep1(int rating, SizeAnswer sizeAnswer, ColorAnswer colorAnswer, MaterialAnswer materialAnswer) {
		this.rating = rating;
		this.sizeAnswer = sizeAnswer;
		this.colorAnswer = colorAnswer;
		this.materialAnswer = materialAnswer;
	}

	public void updateStep2Size(String fitIssueParts) {
		this.fitIssueParts = fitIssueParts;
		this.currentStep = Math.max(this.currentStep, 2);
	}

	public void updateStep2Material(String materialFeatures) {
		this.materialFeatures = materialFeatures;
		this.currentStep = Math.max(this.currentStep, 2);
	}

	public void clearStep2AndStep3() {
		this.fitIssueParts = null;
		this.materialFeatures = null;
		this.sizeReview = null;
		this.materialReview = null;
		this.textReview = null;
		this.reviewImageUrls = null;
	}

	public void submit(String textReview, String imageUrls, String sizeReview, String materialReview, Integer points) {
		this.textReview = textReview;
		this.reviewImageUrls = imageUrls;
		this.sizeReview = sizeReview;
		this.materialReview = materialReview;

		this.reviewStatus = ReviewStatus.COMPLETED;
		this.completedAt = LocalDateTime.now();
		this.currentStep = 4; // 작성 완료 단계
		this.earnedPoints = points;
	}
}