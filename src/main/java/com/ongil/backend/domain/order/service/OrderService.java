package com.ongil.backend.domain.order.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.cart.entity.Cart;
import com.ongil.backend.domain.cart.repository.CartRepository;
import com.ongil.backend.domain.order.converter.OrderConverter;
import com.ongil.backend.domain.order.dto.request.CartOrderRequest;
import com.ongil.backend.domain.order.dto.request.OrderCreateRequest;
import com.ongil.backend.domain.order.dto.request.OrderItemRequest;
import com.ongil.backend.domain.order.dto.response.OrderDetailResponse;
import com.ongil.backend.domain.order.dto.response.OrderHistoryResponse;
import com.ongil.backend.domain.order.dto.response.OrderItemDto;
import com.ongil.backend.domain.order.dto.response.OrderCancelResponse;
import com.ongil.backend.domain.order.entity.Order;
import com.ongil.backend.domain.order.entity.OrderItem;
import com.ongil.backend.domain.order.repository.OrderRepository;
import com.ongil.backend.domain.payment.entity.Payment;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final OrderConverter orderConverter;
	private final CartRepository cartRepository;
	private final com.ongil.backend.domain.payment.repository.PaymentRepository paymentRepository;

	@Transactional
	public Long processPayment(Long userId, OrderCreateRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

		user.decreasePoints(request.usedPoints());

		int totalProductPrice = 0;
		List<OrderItem> orderItems = new ArrayList<>();

		for (OrderItemRequest itemRequest : request.items()) {
			Product product = productRepository.findById(itemRequest.productId())
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

			totalProductPrice += product.getPrice() * itemRequest.quantity();

			OrderItem orderItem = orderConverter.toOrderItem(itemRequest, product);
			orderItems.add(orderItem);
		}

		if (request.usedPoints() < 0 || request.usedPoints() > totalProductPrice) {
			throw new AppException(ErrorCode.INVALID_POINT_USAGE);
		}

		int finalAmount = totalProductPrice - request.usedPoints();
		Order order = orderConverter.toOrder(request, user, finalAmount);

		orderItems.forEach(order::addOrderItem);
		orderRepository.save(order);

		return order.getId();
	}

	@Transactional(readOnly = true)
	public OrderDetailResponse getOrderDetail(Long userId, Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

		if (!order.getUser().getId().equals(userId)) {
			throw new AppException(ErrorCode.FORBIDDEN);
		}

		List<OrderItemDto> itemDtos = order.getOrderItems().stream()
			.map(item -> {
				Product product = item.getProduct();

				String firstImageUrl = "default-image-url";
				if (product.getImageUrls() != null && !product.getImageUrls().isBlank()) {
					firstImageUrl = product.getImageUrls().split(",")[0].trim();
				}

				return new OrderItemDto(
					product.getId(),
					product.getBrand() != null ? product.getBrand().getName() : "일반 브랜드",
					product.getName(),
					firstImageUrl,
					item.getSelectedSize(),
					item.getSelectedColor(),
					item.getQuantity(),
					item.getPriceAtOrder()
				);
			})
			.toList();

		return orderConverter.toDetailResponse(order, itemDtos);
	}

	@Transactional
	public Long createOrderFromCart(Long userId, CartOrderRequest request) {
		List<Cart> cartItems = cartRepository.findAllById(request.cartItemIds());

		if (cartItems.size() != request.cartItemIds().size()) {
			throw new AppException(ErrorCode.CART_NOT_FOUND);
		}

		boolean isNotOwner = cartItems.stream()
			.anyMatch(cart -> !cart.getUser().getId().equals(userId));
		if (isNotOwner) {
			throw new AppException(ErrorCode.CART_FORBIDDEN);
		}

		OrderCreateRequest orderCreateRequest = orderConverter.toOrderCreateRequest(cartItems, request);
		Long orderId = processPayment(userId, orderCreateRequest);

		cartRepository.deleteAllInBatch(cartItems);

		return orderId;
	}

	public OrderHistoryResponse getOrderHistory(
		Long userId,
		String keyword,
		LocalDate startDate,
		LocalDate endDate,
		Pageable pageable
	) {
		// 기본값 설정: endDate가 null이면 오늘, startDate가 null이면 1년 전
		LocalDate effectiveEndDate = (endDate != null) ? endDate : LocalDate.now();
		LocalDate effectiveStartDate = (startDate != null) ? startDate : effectiveEndDate.minusYears(1);

		// LocalDate -> LocalDateTime 변환
		LocalDateTime startDateTime = effectiveStartDate.atStartOfDay();
		LocalDateTime endDateTime = effectiveEndDate.atTime(LocalTime.MAX);

		Page<Order> orderPage = orderRepository.findOrderHistoryWithCount(
			userId,
			keyword,
			startDateTime,
			endDateTime,
			pageable
		);

		return orderConverter.toHistoryResponse(orderPage);
	}

	@Transactional
	public OrderCancelResponse cancelOrder(Long userId, Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

		if (!order.getUser().getId().equals(userId)) {
			throw new AppException(ErrorCode.FORBIDDEN);
		}

		if (order.getOrderStatus() == com.ongil.backend.domain.order.enums.OrderStatus.CANCELED) {
			throw new AppException(ErrorCode.ORDER_ALREADY_CANCELED);
		}

		if (!order.canBeCanceled()) {
			throw new AppException(ErrorCode.ORDER_CANNOT_BE_CANCELED);
		}

		// 주문 취소 처리
		order.cancel();

		// 결제 정보 취소 처리
		Payment payment = order.getPayment();
		if (payment != null) {
			payment.cancel();
			
			// 사용한 포인트 환불
			if (payment.getUsedPoints() > 0) {
				User user = order.getUser();
				user.increasePoints(payment.getUsedPoints());
			}
		}

		return orderConverter.toCancelResponse(order);
	}
}