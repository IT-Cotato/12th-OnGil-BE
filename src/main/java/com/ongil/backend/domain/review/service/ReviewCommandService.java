package com.ongil.backend.domain.review.service;

import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.category.entity.Category;
import com.ongil.backend.domain.order.entity.OrderItem;
import com.ongil.backend.domain.order.repository.OrderItemRepository;
import com.ongil.backend.domain.review.converter.ReviewWriteConverter;
import com.ongil.backend.domain.review.dto.request.AiReviewGenerateRequest;
import com.ongil.backend.domain.review.dto.request.ReviewFinalSubmitRequest;
import com.ongil.backend.domain.review.dto.request.ReviewStep1Request;
import com.ongil.backend.domain.review.dto.request.ReviewStep2MaterialRequest;
import com.ongil.backend.domain.review.dto.request.ReviewStep2SizeRequest;
import com.ongil.backend.domain.review.dto.response.AiReviewResponse;
import com.ongil.backend.domain.review.dto.response.ReviewStep1Response;
import com.ongil.backend.domain.review.entity.Review;
import com.ongil.backend.domain.review.enums.ClothingCategory;
import com.ongil.backend.domain.review.enums.MaterialAnswer;
import com.ongil.backend.domain.review.enums.MaterialFeatureType;
import com.ongil.backend.domain.review.enums.ReviewStatus;
import com.ongil.backend.domain.review.enums.ReviewType;
import com.ongil.backend.domain.review.repository.ReviewRepository;
import com.ongil.backend.domain.review.validator.ReviewValidator;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandService {

	private static final int REVIEW_REWARD_POINTS = 500;

	private final ReviewRepository reviewRepository;
	private final UserRepository userRepository;
	private final OrderItemRepository orderItemRepository;
	private final AiReviewGeneratorService aiReviewGeneratorService;
	private final ReviewValidator reviewValidator;
	private final ReviewWriteConverter reviewWriteConverter;

	@Transactional
	public Long initializeReview(Long userId, Long orderItemId) {
		User user = getUserOrThrow(userId);
		OrderItem orderItem = getOrderItemOrThrow(orderItemId);

		reviewValidator.validateReviewAuthority(orderItem, userId);
		reviewValidator.validateInitialReviewAlreadyCompleted(orderItemId);

		// 이미 DRAFT 리뷰가 있으면 기존 리뷰 ID 반환
		return reviewRepository.findByOrderItemIdAndReviewTypeAndReviewStatus(orderItemId, ReviewType.INITIAL, ReviewStatus.DRAFT)
			.map(Review::getId)
			.orElseGet(() -> {
				Category category = orderItem.getProduct().getCategory();
				String categoryName = (category.getParentCategory() != null)
					? category.getParentCategory().getName()
					: category.getName();
				ClothingCategory clothingCategory = ClothingCategory.fromDisplayName(categoryName);

				Review review = reviewWriteConverter.toInitialReviewEntity(user, orderItem, clothingCategory);
				try {
					return reviewRepository.saveAndFlush(review).getId();
				} catch (DataIntegrityViolationException e) {
					return reviewRepository.findByOrderItemIdAndReviewTypeAndReviewStatus(
						orderItemId, ReviewType.INITIAL, ReviewStatus.DRAFT)
						.map(Review::getId)
						.orElseThrow(() -> new AppException(ErrorCode.INTERNAL_SERVER_ERROR));
				}
			});
	}

	@Transactional
	public ReviewStep1Response updateReviewStep1(Long userId, Long reviewId, ReviewStep1Request request) {
		Review review = getReviewOrThrow(reviewId);
		reviewValidator.validateReviewAuthority(review.getOrderItem(), userId);

		review.clearStep2AndStep3();

		review.updateStep1(
			request.getRating(),
			request.getSizeAnswer(),
			request.getColorAnswer(),
			request.getMaterialAnswer()
		);

		return reviewWriteConverter.toStep1Response(review);
	}

	@Transactional
	public void updateReviewStep2Size(Long userId, Long reviewId, ReviewStep2SizeRequest request) {
		Review review = getReviewOrThrow(reviewId);
		reviewValidator.validateReviewAuthority(review.getOrderItem(), userId);

		String fitIssueParts = String.join(",", request.getFitIssueParts());
		review.updateStep2Size(fitIssueParts);
	}

	@Transactional
	public void updateReviewStep2Material(Long userId, Long reviewId, ReviewStep2MaterialRequest request) {
		Review review = getReviewOrThrow(reviewId);
		reviewValidator.validateReviewAuthority(review.getOrderItem(), userId);

		MaterialAnswer step1Answer = review.getMaterialAnswer();

		String materialFeatures = request.getFeatureTypes().stream()
			.map(type -> {
				if (type == MaterialFeatureType.THICKNESS) {
					return "두께감:선택지전체";
				}

				// 1차 답변에 따라 자동 매핑
				String value = step1Answer.isPositive()
					? type.getValues().get(0)
					: type.getValues().get(1);
				return type.getDisplayName() + ":" + value;
			})
			.collect(Collectors.joining(","));

		review.updateStep2Material(materialFeatures);
	}

	@Transactional(readOnly = true)
	public AiReviewResponse generateSizeAiReview(Long userId, Long reviewId) {
		Review review = getReviewOrThrow(reviewId);
		reviewValidator.validateReviewAuthority(review.getOrderItem(), userId);

		AiReviewGenerateRequest aiRequest = reviewWriteConverter.toSizeAiRequest(review);
		return aiReviewGeneratorService.generateSizeReview(aiRequest);
	}

	@Transactional(readOnly = true)
	public AiReviewResponse generateMaterialAiReview(Long userId, Long reviewId) {
		Review review = getReviewOrThrow(reviewId);
		reviewValidator.validateReviewAuthority(review.getOrderItem(), userId);

		AiReviewGenerateRequest aiRequest = reviewWriteConverter.toMaterialAiRequest(review);
		return aiReviewGeneratorService.generateMaterialReview(aiRequest);
	}

	@Transactional
	public void submitReview(Long userId, Long reviewId, ReviewFinalSubmitRequest request) {
		Review review = getReviewOrThrow(reviewId);
		User user = getUserOrThrow(userId);

		reviewValidator.validateReviewAuthority(review.getOrderItem(), userId);

		String joinedSizeReview = (request.getSizeReview() != null && !request.getSizeReview().isEmpty())
			? String.join("\n", request.getSizeReview()) : null;

		String joinedMaterialReview = (request.getMaterialReview() != null && !request.getMaterialReview().isEmpty())
			? String.join("\n", request.getMaterialReview()) : null;

		String joinedImages = (request.getReviewImageUrls() != null && !request.getReviewImageUrls().isEmpty())
			? String.join(",", request.getReviewImageUrls()) : null;

		review.submit(
			request.getTextReview(),
			joinedImages,
			joinedSizeReview,
			joinedMaterialReview,
			REVIEW_REWARD_POINTS
		);

		user.restorePoints(REVIEW_REWARD_POINTS);
	}

	private Review getReviewOrThrow(Long reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));
	}

	private User getUserOrThrow(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
	}

	private OrderItem getOrderItemOrThrow(Long orderItemId) {
		return orderItemRepository.findById(orderItemId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_ITEM_NOT_FOUND));
	}

}
