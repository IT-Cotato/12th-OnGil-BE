package com.ongil.backend.domain.review.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.order.entity.OrderItem;
import com.ongil.backend.domain.order.repository.OrderItemRepository;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.domain.review.converter.ReviewConverter;
import com.ongil.backend.domain.review.dto.request.ReviewListRequest;
import com.ongil.backend.domain.review.dto.response.*;
import com.ongil.backend.domain.review.entity.Review;
import com.ongil.backend.domain.review.entity.ReviewHelpful;
import com.ongil.backend.domain.review.enums.ColorAnswer;
import com.ongil.backend.domain.review.enums.MaterialAnswer;
import com.ongil.backend.domain.review.enums.ReviewSortType;
import com.ongil.backend.domain.review.enums.ReviewStatus;
import com.ongil.backend.domain.review.enums.ReviewType;
import com.ongil.backend.domain.review.enums.SizeAnswer;
import com.ongil.backend.domain.review.repository.ReviewHelpfulRepository;
import com.ongil.backend.domain.review.repository.ReviewRepository;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryService {

	private final ReviewRepository reviewRepository;
	private final ReviewHelpfulRepository reviewHelpfulRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final OrderItemRepository orderItemRepository;
	private final ReviewConverter reviewConverter;

	private static final int BODY_TYPE_HEIGHT_RANGE = 5;
	private static final int BODY_TYPE_WEIGHT_RANGE = 5;
	private static final int ONE_MONTH_REVIEW_AVAILABLE_DAYS = 5;

	// 1. 상품별 리뷰 목록 조회
	public Page<ReviewListResponse> getProductReviews(Long productId, Long userId, ReviewListRequest request) {
		getProductOrThrow(productId);

		Pageable pageable = createPageable(request.getPage(), request.getPageSize(), request.getSort());
		Page<Review> reviews;

		// 유사 체형 필터링 적용 여부 확인
		User user = (request.isMySizeOnly() && userId != null) ? getUserOrThrow(userId) : null;
		boolean useSimilarBodyType = user != null && hasBodyTypeInfo(user);

		if (useSimilarBodyType) {
			reviews = getReviewsWithSimilarBodyType(
				productId, request.getReviewType(), request.getSize(), request.getColor(),
				user.getHeight(), user.getWeight(), pageable
			);
		} else {
			reviews = getReviewsWithFilters(
				productId, request.getReviewType(), request.getSize(), request.getColor(), pageable
			);
		}

		return reviews.map(review -> {
			boolean isHelpful =
				userId != null && reviewHelpfulRepository.existsByReviewIdAndUserId(review.getId(), userId);
			return reviewConverter.toListResponse(review, isHelpful);
		});
	}

	// 2. 리뷰 통계 요약 조회
	public ReviewSummaryResponse getReviewSummary(Long productId, Long userId) {
		getProductOrThrow(productId);

		Double avgRating = reviewRepository.getAverageRating(productId); // 평균 평점
		Long initialReviewCount = reviewRepository.countByProductIdAndType(productId, ReviewType.INITIAL); // 초기 리뷰 수
		Long oneMonthCount = reviewRepository.countByProductIdAndType(productId, ReviewType.ONE_MONTH); // 한달 후기 수

		// 사이즈 통계 (유사 체형 기준)
		ReviewSummaryResponse.CategorySummary sizeSummary;
		if (userId != null) {
			User user = getUserOrThrow(userId);
			if (hasBodyTypeInfo(user)) {
				sizeSummary = buildSizeSummaryWithSimilarBodyType(productId, user);
			} else {
				sizeSummary = buildCategorySummary("사이즈",
					reviewRepository.countBySizeAnswer(productId, ReviewType.INITIAL));
			}
		} else {
			sizeSummary = buildCategorySummary("사이즈",
				reviewRepository.countBySizeAnswer(productId, ReviewType.INITIAL));
		}

		// 색감, 소재 통계 (전체 유저 기준)
		ReviewSummaryResponse.CategorySummary colorSummary = buildCategorySummary(
			"색감", reviewRepository.countByColorAnswer(productId, ReviewType.INITIAL)
		);
		ReviewSummaryResponse.CategorySummary materialSummary = buildCategorySummary(
			"소재", reviewRepository.countByMaterialAnswer(productId, ReviewType.INITIAL)
		);

		return ReviewSummaryResponse.builder()
			.averageRating(avgRating != null ? Math.round(avgRating * 10) / 10.0 : 0.0)
			.initialReviewCount(initialReviewCount)
			.oneMonthReviewCount(oneMonthCount)
			.sizeSummary(sizeSummary)
			.colorSummary(colorSummary)
			.materialSummary(materialSummary)
			.build();
	}

	// 3. 리뷰 상세 조회
	public ReviewDetailResponse getReviewDetail(Long reviewId, Long userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));

		// 현재 사용자가 이 리뷰에 도움돼요를 눌렀는지 확인
		boolean isHelpful = userId != null && reviewHelpfulRepository.existsByReviewIdAndUserId(reviewId, userId);
		return reviewConverter.toDetailResponse(review, isHelpful);
	}

	// 4. 내가 작성한 리뷰 조회
	public Page<MyReviewResponse> getMyReviews(Long userId, ReviewType reviewType, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

		Page<Review> reviews;
		if (reviewType != null) { // 타입 필터 있음 (일반 or 한달 후)
			reviews = reviewRepository.findByUserIdAndReviewStatusAndReviewType(
				userId, ReviewStatus.COMPLETED, reviewType, pageable
			);
		} else { // 타입 필터 없음 (전체)
			reviews = reviewRepository.findByUserIdAndReviewStatus(userId, ReviewStatus.COMPLETED, pageable);
		}

		return reviews.map(reviewConverter::toMyReviewResponse);
	}

	// 5. 작성 가능한 리뷰 목록 조회
	public List<PendingReviewResponse> getPendingReviews(Long userId) {
		List<PendingReviewResponse> pendingReviews = new ArrayList<>();

		// 사용자의 주문 상품 중 리뷰 작성 가능한 항목 조회
		List<OrderItem> orderItems = orderItemRepository.findByOrderUserIdWithProduct(userId);

		for (OrderItem orderItem : orderItems) {
			LocalDateTime orderDate = orderItem.getOrder().getCreatedAt();

			// 일반 리뷰 작성 가능 여부 확인 (주문 완료 직후)
			if (!reviewRepository.existsByOrderItemIdAndReviewTypeAndReviewStatus(orderItem.getId(), ReviewType.INITIAL, ReviewStatus.COMPLETED)) {
				Review draftReview = reviewRepository.findByOrderItemIdAndReviewTypeAndReviewStatus(
					orderItem.getId(), ReviewType.INITIAL, ReviewStatus.DRAFT).orElse(null);
				pendingReviews.add(reviewConverter.toPendingReviewResponse(orderItem, ReviewType.INITIAL, draftReview));
			}

			// 한달 후 리뷰 작성 가능 여부 확인 (주문 완료 5일 후)
			if (orderDate.plusDays(ONE_MONTH_REVIEW_AVAILABLE_DAYS).isBefore(LocalDateTime.now())) {
				if (!reviewRepository.existsByOrderItemIdAndReviewTypeAndReviewStatus(orderItem.getId(), ReviewType.ONE_MONTH, ReviewStatus.COMPLETED)) {
					Review draftReview = reviewRepository.findByOrderItemIdAndReviewTypeAndReviewStatus(
						orderItem.getId(), ReviewType.ONE_MONTH, ReviewStatus.DRAFT).orElse(null);
					pendingReviews.add(reviewConverter.toPendingReviewResponse(orderItem, ReviewType.ONE_MONTH, draftReview));
				}
			}
		}

		return pendingReviews;
	}

	// 5-1. 작성 가능한 리뷰 개수 조회 (마이페이지 뱃지용)
	public int getPendingReviewCount(Long userId) {
		int count = 0;

		List<OrderItem> orderItems = orderItemRepository.findByOrderUserIdWithProduct(userId);
		List<Long> orderItemIds = orderItems.stream()
			.map(OrderItem::getId)
			.toList();

		if (orderItemIds.isEmpty()) {
			return 0;
		}

		// 이미 작성된 리뷰의 orderItemId 목록을 한 번에 조회 (N+1 방지)
		List<Long> initialReviewedIds = reviewRepository.findReviewedOrderItemIds(orderItemIds, ReviewType.INITIAL);
		List<Long> oneMonthReviewedIds = reviewRepository.findReviewedOrderItemIds(orderItemIds, ReviewType.ONE_MONTH);

		for (OrderItem orderItem : orderItems) {
			LocalDateTime orderDate = orderItem.getOrder().getCreatedAt();

			// 일반 리뷰 미작성 확인
			if (!initialReviewedIds.contains(orderItem.getId())) {
				count++;
			}

			// 한달 후 리뷰 미작성 확인 (주문 완료 5일 후부터 작성 가능)
			if (orderDate.plusDays(ONE_MONTH_REVIEW_AVAILABLE_DAYS).isBefore(LocalDateTime.now())) {
				if (!oneMonthReviewedIds.contains(orderItem.getId())) {
					count++;
				}
			}
		}

		return count;
	}

	// 6. 리뷰 도움돼요 토글
	@Transactional
	public ReviewHelpfulResponse toggleHelpful(Long reviewId, Long userId) {
		// 리뷰에 대한 PESSIMISTIC WRITE 락 획득
		Review review = reviewRepository.findByIdWithLock(reviewId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.REVIEW_NOT_FOUND));

		User user = getUserOrThrow(userId);

		boolean exists = reviewHelpfulRepository.existsByReviewIdAndUserId(reviewId, userId);

		if (exists) {
			reviewHelpfulRepository.deleteByReviewIdAndUserId(reviewId, userId);
			review.decrementHelpfulCount();
		} else {
			ReviewHelpful helpful = ReviewHelpful.builder()
				.review(review)
				.user(user)
				.build();
			reviewHelpfulRepository.save(helpful);
			review.incrementHelpfulCount();
		}

		return ReviewHelpfulResponse.builder()
			.reviewId(reviewId)
			.isHelpful(!exists)
			.helpfulCount(review.getHelpfulCount())
			.build();
	}

	// 정렬 기준에 따른 Pageable 생성
	private Pageable createPageable(int page, int size, ReviewSortType sortType) {
		if (size <= 0) size = 10;
		if (page < 0) page = 0;
		Sort sort = switch (sortType) {
			case BEST -> Sort.by(Sort.Direction.DESC, "helpfulCount");
			case RATING_HIGH -> Sort.by(Sort.Direction.DESC, "rating").and(Sort.by(Sort.Direction.DESC, "createdAt"));
			case RATING_LOW -> Sort.by(Sort.Direction.ASC, "rating").and(Sort.by(Sort.Direction.DESC, "createdAt"));
			case RECENT -> Sort.by(Sort.Direction.DESC, "createdAt");
		};
		return PageRequest.of(page, size, sort);
	}

	// 유사 체형 + 필터 적용 리뷰 조회
	private Page<Review> getReviewsWithSimilarBodyType(
		Long productId, ReviewType reviewType,
		String selectedSize, String selectedColor,
		Integer height, Integer weight, Pageable pageable
	) {
		int minHeight = height - BODY_TYPE_HEIGHT_RANGE;
		int maxHeight = height + BODY_TYPE_HEIGHT_RANGE;
		int minWeight = weight - BODY_TYPE_WEIGHT_RANGE;
		int maxWeight = weight + BODY_TYPE_WEIGHT_RANGE;

		if (selectedSize != null || selectedColor != null) {
			return reviewRepository.findByProductIdWithFiltersAndSimilarBodyType(
				productId, ReviewStatus.COMPLETED, reviewType,
				minHeight, maxHeight, minWeight, maxWeight,
				selectedSize, selectedColor, pageable
			);
		}
		return reviewRepository.findByProductIdAndSimilarBodyType(
			productId, ReviewStatus.COMPLETED, reviewType,
			minHeight, maxHeight, minWeight, maxWeight, pageable
		);
	}

	// 필터만 적용 리뷰 조회
	private Page<Review> getReviewsWithFilters(
		Long productId, ReviewType reviewType,
		String selectedSize, String selectedColor, Pageable pageable
	) {
		if (selectedSize != null || selectedColor != null) {
			return reviewRepository.findByProductIdWithFilters(
				productId, ReviewStatus.COMPLETED, reviewType,
				selectedSize, selectedColor, pageable
			);
		}
		return reviewRepository.findByProductIdAndStatusAndType(
			productId, ReviewStatus.COMPLETED, reviewType, pageable
		);
	}

	// 유사 체형 사용자 사이즈 통계
	private ReviewSummaryResponse.CategorySummary buildSizeSummaryWithSimilarBodyType(Long productId, User user) {
		int minHeight = user.getHeight() - BODY_TYPE_HEIGHT_RANGE;
		int maxHeight = user.getHeight() + BODY_TYPE_HEIGHT_RANGE;
		int minWeight = user.getWeight() - BODY_TYPE_WEIGHT_RANGE;
		int maxWeight = user.getWeight() + BODY_TYPE_WEIGHT_RANGE;

		List<Object[]> stats = reviewRepository.countBySizeAnswerWithSimilarBodyType(
			productId, ReviewType.INITIAL, minHeight, maxHeight, minWeight, maxWeight
		);

		return buildCategorySummary("사이즈", stats);
	}

	// 카테고리별 통계 빌드
	private ReviewSummaryResponse.CategorySummary buildCategorySummary(String category, List<Object[]> stats) {
		if (stats == null || stats.isEmpty()) {
			return ReviewSummaryResponse.CategorySummary.builder()
				.category(category)
				.totalCount(0L)
				.answerStats(List.of())
				.build();
		}

		long totalCount = stats.stream()
			.mapToLong(row -> (Long)row[1])
			.sum();

		List<ReviewSummaryResponse.AnswerStat> answerStats = stats.stream()
			.map(row -> {
				Object key = row[0];
				String answerLabel;

				if (key instanceof SizeAnswer size) answerLabel = size.getDisplayName();
				else if (key instanceof ColorAnswer color) answerLabel = color.getDisplayName();
				else if (key instanceof MaterialAnswer mat) answerLabel = mat.getDisplayName();
				else answerLabel = String.valueOf(key);

				return ReviewSummaryResponse.AnswerStat.builder()
					.answer(answerLabel)
					.count((Long) row[1])
					.percentage(totalCount > 0 ? Math.round(((Long) row[1]) * 1000.0 / totalCount) / 10.0 : 0.0)
					.build();
			})
			.sorted(Comparator.comparing(ReviewSummaryResponse.AnswerStat::getCount).reversed())
			.collect(Collectors.toList());

		String topAnswer = answerStats.isEmpty() ? null : answerStats.get(0).getAnswer();
		Long topAnswerCount = answerStats.isEmpty() ? 0L : answerStats.get(0).getCount();

		return ReviewSummaryResponse.CategorySummary.builder()
			.category(category)
			.totalCount(totalCount)
			.topAnswer(topAnswer)
			.topAnswerCount(topAnswerCount)
			.answerStats(answerStats)
			.build();
	}


	private User getUserOrThrow(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
	}

	private Product getProductOrThrow(Long productId) {
		return productRepository.findById(productId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));
	}

	private boolean hasBodyTypeInfo(User user) {
		return user.getHeight() != null && user.getWeight() != null;
	}
}

