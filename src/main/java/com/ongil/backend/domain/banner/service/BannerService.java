package com.ongil.backend.domain.banner.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

	private static final int MONTHLY_REVIEW_AVAILABLE_DAYS = 5;

	private final OrderRepository orderRepository;
	private final ReviewRepository reviewRepository;
	private final BannerConverter bannerConverter;

	public BannerResponse getBanner(Long userId) {
		// 1순위: 구매직후 후기 미작성
		BannerResponse initialReviewBanner = checkInitialReviewBanner(userId);
		if (initialReviewBanner != null) {
			return initialReviewBanner;
		}

		// 2순위: 한달 후기 미작성 (5일 경과 후 활성화)
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

		if (confirmedOrders.isEmpty()) {
			return null;
		}

		// 모든 OrderItem ID 수집
		List<Long> allOrderItemIds = confirmedOrders.stream()
			.flatMap(order -> order.getOrderItems().stream())
			.map(OrderItem::getId)
			.collect(Collectors.toList());

		if (allOrderItemIds.isEmpty()) {
			return null;
		}

		// 한번의 쿼리로 초기 리뷰 작성된 OrderItem ID 목록 조회
		Set<Long> reviewedOrderItemIds = reviewRepository
			.findReviewedOrderItemIds(allOrderItemIds, ReviewType.INITIAL)
			.stream()
			.collect(Collectors.toSet());

		// 미작성 주문 찾기
		for (Order order : confirmedOrders) {
			for (OrderItem item : order.getOrderItems()) {
				if (!reviewedOrderItemIds.contains(item.getId())) {
					return bannerConverter.toResponse(
						BannerType.REVIEW_PROMPT,
						"구매하신 상품은 어떠셨나요?",
						"작성하러 가기",
						"/review/write",
						item.getId(),
						true
					);
				}
			}
		}

		return null;
	}

	private BannerResponse checkMonthlyReviewBanner(Long userId) {
		LocalDateTime availableDate = LocalDateTime.now().minusDays(MONTHLY_REVIEW_AVAILABLE_DAYS);

		List<Order> orders = orderRepository.findByUserIdAndStatusAndConfirmedAtBefore(
			userId,
			OrderStatus.CONFIRMED,
			availableDate
		);

		if (orders.isEmpty()) {
			return null;
		}

		// 모든 OrderItem ID 수집
		List<Long> allOrderItemIds = orders.stream()
			.flatMap(order -> order.getOrderItems().stream())
			.map(OrderItem::getId)
			.collect(Collectors.toList());

		if (allOrderItemIds.isEmpty()) {
			return null;
		}

		// 한번의 쿼리로 한달 후기 작성된 OrderItem ID 목록 조회
		Set<Long> reviewedOrderItemIds = reviewRepository
			.findReviewedOrderItemIds(allOrderItemIds, ReviewType.ONE_MONTH)
			.stream()
			.collect(Collectors.toSet());

		// 미작성 주문 찾기
		for (Order order : orders) {
			for (OrderItem item : order.getOrderItems()) {
				if (!reviewedOrderItemIds.contains(item.getId())) {
					return bannerConverter.toResponse(
						BannerType.MONTHLY_REVIEW_PROMPT,
						"한달 후기를 작성해주세요!",
						"작성하러 가기",
						"/review/monthly/write",
						item.getId(),
						true
					);
				}
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
}