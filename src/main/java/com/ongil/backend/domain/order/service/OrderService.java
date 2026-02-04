package com.ongil.backend.domain.order.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.cart.entity.Cart;
import com.ongil.backend.domain.cart.repository.CartRepository;
import com.ongil.backend.domain.order.converter.OrderConverter;
import com.ongil.backend.domain.order.dto.request.CartOrderRequest;
import com.ongil.backend.domain.order.dto.request.OrderCreateRequest;
import com.ongil.backend.domain.order.dto.request.OrderItemRequest;
import com.ongil.backend.domain.order.dto.response.OrderDetailResponse;
import com.ongil.backend.domain.order.dto.response.OrderItemDto;
import com.ongil.backend.domain.order.dto.response.OrderListResponse;
import com.ongil.backend.domain.order.entity.Order;
import com.ongil.backend.domain.order.entity.OrderItem;
import com.ongil.backend.domain.order.repository.OrderRepository;
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
			.orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND));

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

	@Transactional(readOnly = true)
	public List<OrderListResponse> getUserOrders(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

		List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

		return orders.stream()
			.map(orderConverter::toListResponse)
			.toList();
	}
}