package com.ongil.backend.domain.review.converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.order.entity.OrderItem;
import com.ongil.backend.domain.review.dto.request.AiReviewGenerateRequest;
import com.ongil.backend.domain.review.dto.response.ReviewStep1Response;
import com.ongil.backend.domain.review.entity.Review;
import com.ongil.backend.domain.review.enums.ClothingCategory;
import com.ongil.backend.domain.review.enums.ReviewStatus;
import com.ongil.backend.domain.review.enums.ReviewType;
import com.ongil.backend.domain.user.entity.User;

@Component
public class ReviewWriteConverter {

	public Review toInitialReviewEntity(User user, OrderItem orderItem, ClothingCategory category) {
		return Review.builder()
			.user(user)
			.orderItem(orderItem)
			.product(orderItem.getProduct())
			.clothingCategory(category)
			.reviewStatus(ReviewStatus.DRAFT)
			.reviewType(ReviewType.INITIAL)
			.build();
	}

	public ReviewStep1Response toStep1Response(Review review) {
		boolean needsSizeQ = review.getSizeAnswer().isNeedsSecondaryQuestion();
		boolean needsMaterialQ = review.getMaterialAnswer().isNeedsSecondaryQuestion();

		List<String> availableBodyParts = needsSizeQ
			? review.getClothingCategory().getBodyParts()
			: Collections.emptyList();

		return ReviewStep1Response.of(
			review.getId(),
			needsSizeQ,
			needsMaterialQ,
			availableBodyParts
		);
	}

	public AiReviewGenerateRequest toMaterialAiRequest(Review review) {
		return AiReviewGenerateRequest.builder()
			.reviewId(review.getId())
			.clothingType(review.getClothingCategory())
			.materialAnswer(review.getMaterialAnswer())
			.materialFeatures(review.getMaterialFeatures() != null ?
				Arrays.asList(review.getMaterialFeatures().split(",")) : Collections.emptyList())
			.build();
	}

	public AiReviewGenerateRequest toSizeAiRequest(Review review) {
		return AiReviewGenerateRequest.builder()
			.reviewId(review.getId())
			.clothingType(review.getClothingCategory())
			.sizeAnswer(review.getSizeAnswer())
			.fitIssueParts(review.getFitIssueParts() != null ?
				Arrays.asList(review.getFitIssueParts().split(",")) : Collections.emptyList())
			.build();
	}
}
