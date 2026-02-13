package com.ongil.backend.domain.review.validator;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.order.entity.OrderItem;
import com.ongil.backend.domain.review.enums.MaterialAnswer;
import com.ongil.backend.domain.review.enums.ReviewStatus;
import com.ongil.backend.domain.review.enums.ReviewType;
import com.ongil.backend.domain.review.enums.SizeAnswer;
import com.ongil.backend.domain.review.repository.ReviewRepository;
import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.common.exception.ForbiddenException;
import com.ongil.backend.global.common.exception.ValidationException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewValidator {

	private final ReviewRepository reviewRepository;

	// 리뷰 작성 권한
	public void validateReviewAuthority(OrderItem orderItem, Long userId) {
		if (!orderItem.getOrder().getUser().getId().equals(userId)) {
			throw new ForbiddenException(ErrorCode.FORBIDDEN);
		}
	}

	// 1차 답변 및 2차 답변 세트 검증
	public void validateReviewStepCompletion(SizeAnswer sizeAnswer, List<String> fitIssueParts) {
		if (sizeAnswer == null) {
			throw new ValidationException(ErrorCode.REVIEW_STEP1_INCOMPLETE);
		}

		if (sizeAnswer.isNeedsSecondaryQuestion()) {
			if (fitIssueParts == null || fitIssueParts.isEmpty()) {
				throw new ValidationException(ErrorCode.REVIEW_STEP2_INCOMPLETE);
			}
		}
	}

	public void validateReviewStepCompletion(MaterialAnswer materialAnswer, List<String> materialFeatures) {
		if (materialAnswer == null) {
			throw new ValidationException(ErrorCode.REVIEW_STEP1_INCOMPLETE);
		}

		if (materialAnswer.isNeedsSecondaryQuestion()) {
			if (materialFeatures == null || materialFeatures.isEmpty()) {
				throw new ValidationException(ErrorCode.REVIEW_STEP2_INCOMPLETE);
			}
		}
	}

	public void validateInitialReviewAlreadyExists(Long orderItemId) {
		if (reviewRepository.existsByOrderItemIdAndReviewTypeAndReviewStatus(
			orderItemId, ReviewType.INITIAL, ReviewStatus.COMPLETED)) {
			throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
		}
	}
}
