package com.ongil.backend.domain.order.service;

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

		int totalProductPrice = request.items().stream()
			.mapToInt(item -> item.priceAtOrder() * item.quantity())
			.sum();
		int finalAmount = totalProductPrice - request.usedPoints();

		Order order = orderConverter.toOrder(request, user, finalAmount);

		for (OrderItemRequest itemRequest : request.items()) {
			Product product = productRepository.findById(itemRequest.productId())
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

			OrderItem orderItem = orderConverter.toOrderItem(itemRequest, product, order);
			order.addOrderItem(orderItem);
		}

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
			.map(item -> new OrderItemDto(
				item.getProduct().getBrand() != null ? item.getProduct().getBrand().getName() : "일반 브랜드",
				item.getProduct().getName(),
				item.getSelectedSize(),
				item.getSelectedColor(),
				item.getQuantity(),
				item.getPriceAtOrder()
			))
			.toList();

		return orderConverter.toDetailResponse(order, itemDtos);
	}

	@Transactional
	public Long createOrderFromCart(Long userId, CartOrderRequest request) {
		List<Cart> cartItems = cartRepository.findAllById(request.cartItemIds());

		if (cartItems.isEmpty()) {
			throw new AppException(ErrorCode.CART_EMPTY);
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
}