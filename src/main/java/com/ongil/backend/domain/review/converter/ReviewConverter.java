package com.ongil.backend.domain.review.converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.category.entity.Category;
import com.ongil.backend.domain.order.entity.OrderItem;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.review.dto.response.MyReviewResponse;
import com.ongil.backend.domain.review.dto.response.PendingReviewResponse;
import com.ongil.backend.domain.review.dto.response.ReviewDetailResponse;
import com.ongil.backend.domain.review.dto.response.ReviewListResponse;
import com.ongil.backend.domain.review.entity.Review;
import com.ongil.backend.domain.review.enums.ClothingCategory;
import com.ongil.backend.domain.review.enums.MaterialAnswer;
import com.ongil.backend.domain.review.enums.ReviewType;
import com.ongil.backend.domain.review.enums.SizeAnswer;
import com.ongil.backend.domain.user.entity.User;

@Component
public class ReviewConverter {

	/*
	 리뷰 목록 응답 변환
	 */
	public ReviewListResponse toListResponse(Review review, boolean isHelpful) {
		User user = review.getUser();
		OrderItem orderItem = review.getOrderItem();

		return ReviewListResponse.builder()
			.reviewId(review.getId())
			.reviewType(review.getReviewType().name())
			.rating(review.getRating())
			.helpfulCount(review.getHelpfulCount())
			.isHelpful(isHelpful)
			.sizeReview(parseToList(review.getSizeReview(), "\n"))
			.materialReview(parseToList(review.getMaterialReview(), "\n"))
			.textReview(review.getTextReview())
			.reviewImageUrls(parseImageUrls(review.getReviewImageUrls()))
			.reviewer(buildReviewerInfo(user, review.getProduct()))
			.purchaseOption(buildPurchaseOption(orderItem))
			.answers(buildAnswerSummary(review))
			.createdAt(review.getCreatedAt())
			.build();
	}

	// 작성자 정보 구성
	private ReviewListResponse.ReviewerInfo buildReviewerInfo(User user, Product product) {
		// 상품 타입에 따라 평소 사이즈 결정 (의류 기준 상의 사이즈)
		String usualSize = user.getUsualTopSize();

		return ReviewListResponse.ReviewerInfo.builder()
			.height(user.getHeight())
			.weight(user.getWeight())
			.usualSize(usualSize)
			.build();
	}

	// 구매 옵션 정보 구성
	private ReviewListResponse.PurchaseOption buildPurchaseOption(OrderItem orderItem) {
		return ReviewListResponse.PurchaseOption.builder()
			.selectedColor(orderItem.getSelectedColor())
			.selectedSize(orderItem.getSelectedSize())
			.build();
	}

	// 선택지 답변 요약 구성
	private ReviewListResponse.AnswerSummary buildAnswerSummary(Review review) {
		return ReviewListResponse.AnswerSummary.builder()
			.sizeAnswer(review.getSizeAnswer().getDisplayName())
			.colorAnswer(review.getColorAnswer().getDisplayName())
			.materialAnswer(review.getMaterialAnswer().getDisplayName())
			.build();
	}

	/*
	 리뷰 상세 응답 변환
	 */
	public ReviewDetailResponse toDetailResponse(Review review, boolean isHelpful) {
		User user = review.getUser();
		OrderItem orderItem = review.getOrderItem();
		Product product = review.getProduct();

		ReviewDetailResponse.ReviewDetailResponseBuilder builder = ReviewDetailResponse.builder()
			.reviewId(review.getId())
			.reviewStatus(review.getReviewStatus().name())
			.reviewType(review.getReviewType().name())
			.rating(review.getRating())
			.helpfulCount(review.getHelpfulCount())
			.isHelpful(isHelpful)
			.sizeReview(parseToList(review.getSizeReview(), "\n"))
			.materialReview(parseToList(review.getMaterialReview(), "\n"))
			.textReview(review.getTextReview())
			.reviewImageUrls(parseImageUrls(review.getReviewImageUrls()))
			.reviewer(buildDetailReviewerInfo(user))
			.purchaseOption(buildDetailPurchaseOption(orderItem))
			.product(buildProductInfo(product))
			.createdAt(review.getCreatedAt())
			.completedAt(review.getCompletedAt());

		// 리뷰 타입에 따라 답변 설정
		if (review.getReviewType() == ReviewType.INITIAL) {
			builder.initialFirstAnswers(buildInitialFirstAnswers(review));
			builder.initialSecondAnswers(buildInitialSecondAnswers(review));
		} else {
			builder.oneMonthAnswers(buildOneMonthAnswers(review));
		}

		return builder.build();
	}

	// 상세 작성자 정보 구성
	private ReviewDetailResponse.ReviewerInfo buildDetailReviewerInfo(User user) {
		return ReviewDetailResponse.ReviewerInfo.builder()
			.height(user.getHeight())
			.weight(user.getWeight())
			.usualTopSize(user.getUsualTopSize())
			.usualBottomSize(user.getUsualBottomSize())
			.usualShoeSize(user.getUsualShoeSize())
			.build();
	}

	// 상세 구매 옵션 정보 구성
	private ReviewDetailResponse.PurchaseOption buildDetailPurchaseOption(OrderItem orderItem) {
		return ReviewDetailResponse.PurchaseOption.builder()
			.selectedColor(orderItem.getSelectedColor())
			.selectedSize(orderItem.getSelectedSize())
			.build();
	}

	// 상품 정보 구성
	private ReviewDetailResponse.ProductInfo buildProductInfo(Product product) {
		return ReviewDetailResponse.ProductInfo.builder()
			.productId(product.getId())
			.productName(product.getName())
			.clothingCategory(resolveClothingCategory(product))
			.brandName(product.getBrand() != null ? product.getBrand().getName() : null)
			.thumbnailImageUrl(getFirstImage(product.getImageUrls()))
			.build();
	}

	// 1차 리뷰 답변 구성
	private ReviewDetailResponse.InitialFirstAnswers buildInitialFirstAnswers(Review review) {
		if (review.getSizeAnswer() == null && review.getColorAnswer() == null && review.getMaterialAnswer() == null) {
			return null;
		}

		SizeAnswer sizeAnswer = review.getSizeAnswer();
		MaterialAnswer materialAnswer = review.getMaterialAnswer();

		String sizeSecondaryType = null;
		if (sizeAnswer != null && sizeAnswer.isNeedsSecondaryQuestion()) {
			sizeSecondaryType = (sizeAnswer == SizeAnswer.LOOSE || sizeAnswer == SizeAnswer.TOO_BIG_NEED_ALTERATION)
				? "POSITIVE" : "NEGATIVE";
		}

		String materialSecondaryType = null;
		if (materialAnswer != null && materialAnswer.isNeedsSecondaryQuestion()) {
			materialSecondaryType = materialAnswer.isPositive() ? "POSITIVE" : "NEGATIVE";
		}

		return ReviewDetailResponse.InitialFirstAnswers.builder()
			.sizeAnswer(sizeAnswer != null ? sizeAnswer.name() : null)
			.sizeSecondaryType(sizeSecondaryType)
			.colorAnswer(review.getColorAnswer() != null ? review.getColorAnswer().name() : null)
			.materialAnswer(materialAnswer != null ? materialAnswer.name() : null)
			.materialSecondaryType(materialSecondaryType)
			.build();
	}

	// 2차 리뷰 답변 구성
	private ReviewDetailResponse.InitialSecondAnswers buildInitialSecondAnswers(Review review) {
		return ReviewDetailResponse.InitialSecondAnswers.builder()
			.fitIssueParts(parseToList(review.getFitIssueParts(), ","))
			.materialFeatures(parseToList(review.getMaterialFeatures(), ","))
			.build();
	}

	// 한달 후 리뷰 답변 구성
	private ReviewDetailResponse.OneMonthAnswers buildOneMonthAnswers(Review review) {
		return ReviewDetailResponse.OneMonthAnswers.builder()
			.overall(review.getOneMonthOverall())
			.changes(review.getOneMonthChanges())
			.build();
	}

	/*
	 내가 작성한 리뷰 응답 변환
	 */
	public MyReviewResponse toMyReviewResponse(Review review) {
		OrderItem orderItem = review.getOrderItem();
		Product product = review.getProduct();

		MyReviewResponse.MyReviewResponseBuilder builder = MyReviewResponse.builder()
			.reviewId(review.getId())
			.reviewType(review.getReviewType().name())
			.rating(review.getRating())
			.helpfulCount(review.getHelpfulCount())
			.sizeReview(parseToList(review.getSizeReview(), "\n"))
			.materialReview(parseToList(review.getMaterialReview(), "\n"))
			.textReview(review.getTextReview())
			.reviewImageUrls(parseImageUrls(review.getReviewImageUrls()))
			.product(buildMyReviewProductInfo(product))
			.purchaseOption(formatPurchaseOption(orderItem))
			.earnedPoints(review.getEarnedPoints())
			.createdAt(review.getCreatedAt());

		if (review.getReviewType() == ReviewType.INITIAL) {
			builder.answers(buildMyReviewAnswerSummary(review));
		} else {
			builder.oneMonthAnswers(buildMyOneMonthAnswers(review));
		}

		return builder.build();
	}

	// 내가 작성한 리뷰 - 상품 정보 구성
	private MyReviewResponse.ProductInfo buildMyReviewProductInfo(Product product) {
		return MyReviewResponse.ProductInfo.builder()
			.productId(product.getId())
			.productName(product.getName())
			.brandName(product.getBrand() != null ? product.getBrand().getName() : null)
			.thumbnailImageUrl(getFirstImage(product.getImageUrls()))
			.build();
	}

	// 내가 작성한 리뷰 - 선택지 답변 요약 구성(2차 답변도 포함)
	private MyReviewResponse.AnswerSummary buildMyReviewAnswerSummary(Review review) {
		return MyReviewResponse.AnswerSummary.builder()
			.sizeAnswer(review.getSizeAnswer().getDisplayName())
			.colorAnswer(review.getColorAnswer().getDisplayName())
			.materialAnswer(review.getMaterialAnswer().getDisplayName())
			.fitIssueParts(review.getFitIssueParts())
			.materialFeatures(review.getMaterialFeatures())
			.build();
	}

	// 내가 작성한 리뷰 - 한달 후 답변 구성
	private MyReviewResponse.OneMonthAnswers buildMyOneMonthAnswers(Review review) {
		return MyReviewResponse.OneMonthAnswers.builder()
			.overall(review.getOneMonthOverall())
			.changes(review.getOneMonthChanges())
			.build();
	}

	/*
	  작성 가능한 리뷰 응답 변환
	 */
	public PendingReviewResponse toPendingReviewResponse(
		OrderItem orderItem, ReviewType availableType, Review draftReview
	) {
		Product product = orderItem.getProduct();

		return PendingReviewResponse.builder()
			.orderItemId(orderItem.getId())
			.availableReviewType(availableType.name())
			.reviewStatus(draftReview != null ? draftReview.getReviewStatus().name() : null)
			.reviewId(draftReview != null ? draftReview.getId() : null)
			.product(buildPendingProductInfo(product))
			.purchaseOption(formatPurchaseOption(orderItem))
			.orderedAt(orderItem.getOrder().getCreatedAt())
			.reviewAvailableAt(
				availableType == ReviewType.INITIAL
					? orderItem.getOrder().getCreatedAt()
					: orderItem.getOrder().getCreatedAt().plusDays(5)
			)
			.build();
	}

	// 작성 가능한 리뷰 - 상품 정보 구성
	private PendingReviewResponse.ProductInfo buildPendingProductInfo(Product product) {
		return PendingReviewResponse.ProductInfo.builder()
			.productId(product.getId())
			.productName(product.getName())
			.clothingCategory(resolveClothingCategory(product))
			.brandName(product.getBrand() != null ? product.getBrand().getName() : null)
			.thumbnailImageUrl(getFirstImage(product.getImageUrls()))
			.build();
	}

	private ClothingCategory resolveClothingCategory(Product product) {
		return Optional.ofNullable(product.getCategory())
			.map(Category::getParentCategory)
			.map(Category::getName)
			.map(ClothingCategory::fromDisplayName)
			.orElse(null);
	}

	// 공통 유틸리티 메서드
	private List<String> parseImageUrls(String imageUrls) {
		if (imageUrls == null || imageUrls.trim().isEmpty()) {
			return Collections.emptyList();
		}
		return Arrays.stream(imageUrls.split(","))
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.collect(Collectors.toList());
	}

	private String getFirstImage(String imageUrls) {
		List<String> urls = parseImageUrls(imageUrls);
		return urls.isEmpty() ? null : urls.get(0);
	}

	private String formatPurchaseOption(OrderItem orderItem) {
		String color = orderItem.getSelectedColor();
		String size = orderItem.getSelectedSize();
		if (color != null && size != null) {
			return color + "/" + size;
		} else if (color != null) {
			return color;
		} else if (size != null) {
			return size;
		}
		return null;
	}

	private List<String> parseToList(String content, String delimiter) {
		if (content == null || content.isBlank()) {
			return Collections.emptyList();
		}
		return Arrays.stream(content.split(delimiter))
			.map(String::trim)
			.filter(s -> !s.isEmpty())
			.toList();
	}

}