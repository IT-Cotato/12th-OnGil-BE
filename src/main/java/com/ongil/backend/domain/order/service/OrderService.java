package com.ongil.backend.domain.order.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.address.entity.Address;
import com.ongil.backend.domain.address.repository.AddressRepository;
import com.ongil.backend.domain.cart.dto.request.CartCreateRequest;
import com.ongil.backend.domain.cart.entity.Cart;
import com.ongil.backend.domain.cart.repository.CartRepository;
import com.ongil.backend.domain.cart.service.CartService;
import com.ongil.backend.domain.order.converter.OrderConverter;
import com.ongil.backend.domain.order.dto.request.CartOrderRequest;
import com.ongil.backend.domain.order.dto.request.DeliveryAddressUpdateRequest;
import com.ongil.backend.domain.order.dto.request.OrderCancelRequest;
import com.ongil.backend.domain.order.dto.request.OrderCreateRequest;
import com.ongil.backend.domain.order.dto.request.OrderItemRequest;
import com.ongil.backend.domain.order.dto.response.CancelRefundInfoResponse;
import com.ongil.backend.domain.order.dto.response.OrderCancelResponse;
import com.ongil.backend.domain.order.dto.response.OrderDetailResponse;
import com.ongil.backend.domain.order.dto.response.OrderHistoryResponse;
import com.ongil.backend.domain.order.dto.response.OrderItemDto;
import com.ongil.backend.domain.order.dto.response.RefundInfoDto;
import com.ongil.backend.domain.order.entity.Order;
import com.ongil.backend.domain.order.entity.OrderItem;
import com.ongil.backend.domain.order.enums.OrderStatus;
import com.ongil.backend.domain.order.repository.OrderRepository;
import com.ongil.backend.domain.payment.entity.Payment;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.entity.ProductOption;
import com.ongil.backend.domain.product.repository.ProductOptionRepository;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

	private final OrderRepository orderRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;
	private final OrderConverter orderConverter;
	private final CartRepository cartRepository;
	private final CartService cartService;
	private final AddressRepository addressRepository;
	private final ProductOptionRepository productOptionRepository;

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

			totalProductPrice += product.getEffectivePrice() * itemRequest.quantity();

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

		List<OrderItemDto> itemDtos = orderConverter.toOrderItemDtos(order);

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

	public CancelRefundInfoResponse getRefundInfo(Long userId, Long orderId) {
		Order order = getOrderAndValidateOwner(userId, orderId);
		validateCancelable(order);

		List<OrderItemDto> itemDtos = orderConverter.toOrderItemDtos(order);
		RefundInfoDto refundInfo = calculateRefundInfo(order);

		return new CancelRefundInfoResponse(itemDtos, refundInfo);
	}

	@Transactional
	public OrderCancelResponse cancelOrder(Long userId, Long orderId, OrderCancelRequest request) {
		Order order = getOrderAndValidateOwner(userId, orderId);
		validateCancelable(order);

		// 1. 주문 취소 처리
		order.cancel(request.cancelReason(), request.cancelDetail());

		// 2. 결제 취소 처리
		Payment payment = order.getPayment();
		if (payment != null) {
			payment.cancelPayment();

			// 3. 포인트 복원
			if (payment.getUsedPoints() != null && payment.getUsedPoints() > 0) {
				order.getUser().restorePoints(payment.getUsedPoints());
			}
		}

		// 4. 재고 복원
		for (OrderItem orderItem : order.getOrderItems()) {
			Optional<ProductOption> optionOpt = productOptionRepository.findByProductIdAndSizeAndColor(
				orderItem.getProduct().getId(),
				orderItem.getSelectedSize(),
				orderItem.getSelectedColor()
			);
			if (optionOpt.isPresent()) {
				optionOpt.get().restoreStock(orderItem.getQuantity());
			} else {
				log.warn("재고 복원 실패 - productId: {}, size: {}, color: {}",
					orderItem.getProduct().getId(),
					orderItem.getSelectedSize(),
					orderItem.getSelectedColor());
			}
		}

		// 5. 장바구니 담기 (선택)
		if (Boolean.TRUE.equals(request.addToCart())) {
			for (OrderItem orderItem : order.getOrderItems()) {
				CartCreateRequest cartRequest = new CartCreateRequest(
					orderItem.getProduct().getId(),
					orderItem.getSelectedSize(),
					orderItem.getSelectedColor(),
					orderItem.getQuantity()
				);
				cartService.addCart(userId, cartRequest);
			}
		}

		List<OrderItemDto> itemDtos = orderConverter.toOrderItemDtos(order);
		RefundInfoDto refundInfo = calculateRefundInfo(order);

		return orderConverter.toCancelResponse(order, itemDtos, refundInfo);
	}

	@Transactional
	public OrderDetailResponse updateDeliveryAddress(Long userId, Long orderId, DeliveryAddressUpdateRequest request) {
		Order order = getOrderAndValidateOwner(userId, orderId);

		if (order.getOrderStatus() != OrderStatus.CONFIRMED) {
			throw new AppException(ErrorCode.ORDER_UPDATE_NOT_ALLOWED);
		}

		Address address = addressRepository.findById(request.addressId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.ADDRESS_NOT_FOUND));

		if (!address.getUser().getId().equals(userId)) {
			throw new AppException(ErrorCode.ADDRESS_FORBIDDEN);
		}

		order.updateDeliveryAddress(
			address.getRecipientName(),
			address.getRecipientPhone(),
			address.getBaseAddress(),
			address.getDetailAddress(),
			address.getPostalCode(),
			address.getDeliveryRequest()
		);

		List<OrderItemDto> itemDtos = orderConverter.toOrderItemDtos(order);
		return orderConverter.toDetailResponse(order, itemDtos);
	}

	@Transactional
	public void deleteOrder(Long userId, Long orderId) {
		Order order = getOrderAndValidateOwner(userId, orderId);

		if (order.getOrderStatus() != OrderStatus.CONFIRMED && order.getOrderStatus() != OrderStatus.CANCELED) {
			throw new AppException(ErrorCode.ORDER_DELETE_NOT_ALLOWED);
		}

		orderRepository.delete(order);
	}

	private Order getOrderAndValidateOwner(Long userId, Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.ORDER_NOT_FOUND));

		if (!order.getUser().getId().equals(userId)) {
			throw new AppException(ErrorCode.FORBIDDEN);
		}

		return order;
	}

	private void validateCancelable(Order order) {
		if (order.getOrderStatus() == OrderStatus.CANCELED) {
			throw new AppException(ErrorCode.ORDER_ALREADY_CANCELED);
		}
		if (order.getOrderStatus() != OrderStatus.CONFIRMED) {
			throw new AppException(ErrorCode.ORDER_CANCEL_NOT_ALLOWED);
		}
	}

	private RefundInfoDto calculateRefundInfo(Order order) {
		int productAmount = order.getOrderItems().stream()
			.mapToInt(item -> item.getPriceAtOrder() * item.getQuantity())
			.sum();
		int shippingFee = 0;

		int usedPoints = 0;
		Payment payment = order.getPayment();
		if (payment != null && payment.getUsedPoints() != null) {
			usedPoints = payment.getUsedPoints();
		}

		int refundAmount = Math.max(productAmount - shippingFee - usedPoints, 0);

		return new RefundInfoDto(productAmount, shippingFee, usedPoints, refundAmount);
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
}