package com.ongil.backend.domain.banner.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.banner.converter.BannerConverter;
import com.ongil.backend.domain.banner.dto.response.BannerResponse;
import com.ongil.backend.domain.banner.enums.BannerType;
import com.ongil.backend.domain.order.entity.Order;
import com.ongil.backend.domain.order.entity.OrderItem;
import com.ongil.backend.domain.order.enums.OrderStatus;
import com.ongil.backend.domain.order.repository.OrderRepository;
import com.ongil.backend.domain.review.enums.ReviewType;
import com.ongil.backend.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService {

	private final OrderRepository orderRepository;
	private final ReviewRepository reviewRepository;
	private final BannerConverter bannerConverter;

	public BannerResponse getBanner(Long userId) {
		// 1순위: 구매직후 후기 미작성
		BannerResponse initialReviewBanner = checkInitialReviewBanner(userId);
		if (initialReviewBanner != null) {
			return initialReviewBanner;
		}

		// 2순위: 한달 후 후기 미작성
		BannerResponse monthlyReviewBanner = checkMonthlyReviewBanner(userId);
		if (monthlyReviewBanner != null) {
			return monthlyReviewBanner;
		}

		// 3순위: 매거진 유도 알림
		return createMagazineBanner();
	}

	private BannerResponse checkInitialReviewBanner(Long userId) {
		List<Order> confirmedOrders = orderRepository.findByUserIdAndStatus(
			userId,
			OrderStatus.CONFIRMED
		);

		for (Order order : confirmedOrders) {
			OrderItem pendingItem = findPendingInitialReviewItem(order);
			if (pendingItem != null) {
				return bannerConverter.toResponse(
					BannerType.REVIEW_PROMPT,
					"구매하신 상품은 어떠셨나요?",
					"작성하러 가기",
					"/review/write",
					order.getId(),
					true
				);
			}
		}

		return null;
	}

	private BannerResponse checkMonthlyReviewBanner(Long userId) {
		LocalDateTime fiveDaysAgo = LocalDateTime.now().minusDays(5);

		List<Order> orders = orderRepository.findByUserIdAndStatusAndConfirmedAtBefore(
			userId,
			OrderStatus.CONFIRMED,
			fiveDaysAgo
		);

		for (Order order : orders) {
			OrderItem pendingItem = findPendingMonthlyReviewItem(order);
			if (pendingItem != null) {
				return bannerConverter.toResponse(
					BannerType.MONTHLY_REVIEW_PROMPT,
					"한달 후기를 작성해주세요!",
					"작성하러 가기",
					"/review/monthly/write",
					order.getId(),
					true
				);
			}
		}

		return null;
	}

	private BannerResponse createMagazineBanner() {
		return bannerConverter.toResponse(
			BannerType.MAGAZINE,
			"추천 매거진을 확인해보세요",
			"보러가기",
			"/magazine",
			null,
			true
		);
	}

	private OrderItem findPendingInitialReviewItem(Order order) {
		for (OrderItem item : order.getOrderItems()) {
			boolean hasInitialReview = reviewRepository.existsByOrderItemIdAndReviewType(
				item.getId(),
				ReviewType.INITIAL
			);
			if (!hasInitialReview) {
				return item;
			}
		}
		return null;
	}

	private OrderItem findPendingMonthlyReviewItem(Order order) {
		for (OrderItem item : order.getOrderItems()) {
			boolean hasMonthlyReview = reviewRepository.existsByOrderItemIdAndReviewType(
				item.getId(),
				ReviewType.ONE_MONTH
			);
			if (!hasMonthlyReview) {
				return item;
			}
		}
		return null;
	}
}