package com.ongil.backend.domain.order.converter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.cart.entity.Cart;
import com.ongil.backend.domain.order.dto.request.CartOrderRequest;
import com.ongil.backend.domain.order.dto.request.OrderCreateRequest;
import com.ongil.backend.domain.order.dto.request.OrderItemRequest;
import com.ongil.backend.domain.order.dto.response.OrderDetailResponse;
import com.ongil.backend.domain.order.dto.response.OrderItemDto;
import com.ongil.backend.domain.order.entity.Order;
import com.ongil.backend.domain.order.entity.OrderItem;
import com.ongil.backend.domain.order.enums.OrderStatus;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.user.entity.User;

@Component
public class OrderConverter {

	// Request -> Order 엔티티
	public Order toOrder(OrderCreateRequest request, User user, int finalAmount) {
		return Order.builder()
			.orderNumber("ORD-" + System.currentTimeMillis())
			.totalAmount(finalAmount)
			.recipient(request.recipient())
			.recipientPhone(request.recipientPhone())
			.deliveryAddress(request.deliveryAddress())
			.detailAddress(request.detailAddress())
			.postalCode(request.postalCode())
			.deliveryMessage(request.deliveryMessage())
			.orderStatus(OrderStatus.ORDER_RECEIVED)
			.user(user)
			.build();
	}

	// RequestItem -> OrderItem 엔티티
	public OrderItem toOrderItem(OrderItemRequest itemRequest, Product product, Order order) {
		return OrderItem.builder()
			.product(product)
			.order(order)
			.selectedSize(itemRequest.selectedSize())
			.selectedColor(itemRequest.selectedColor())
			.quantity(itemRequest.quantity())
			.priceAtOrder(itemRequest.priceAtOrder())
			.build();
	}

	// Order 엔티티 -> Response DTO
	public OrderDetailResponse toDetailResponse(Order order, List<OrderItemDto> itemDtos) {
		return new OrderDetailResponse(
			order.getId(),
			order.getOrderNumber(),
			itemDtos,
			order.getTotalAmount(),
			order.getRecipient(),
			order.getRecipientPhone(),
			order.getDeliveryAddress() + " " + (order.getDetailAddress() != null ? order.getDetailAddress() : ""),
			order.getDeliveryMessage(),
			order.getCreatedAt()
		);
	}

	// CartItem 리스트 -> OrderCreateRequest
	public OrderCreateRequest toOrderCreateRequest(List<Cart> cartItems, CartOrderRequest request) {
		// 1. OrderItemRequest 리스트 생성
		List<OrderItemRequest> orderItemRequests = cartItems.stream()
			.map(cartItem -> new OrderItemRequest(
				cartItem.getProduct().getId(),
				cartItem.getSelectedSize(),
				cartItem.getSelectedColor(),
				cartItem.getQuantity(),
				cartItem.getProduct().getPrice()
			))
			.toList();

		// 2. 총 상품 금액 계산
		int totalProductPrice = cartItems.stream()
			.mapToInt(ci -> ci.getProduct().getPrice() * ci.getQuantity())
			.sum();

		// 3. 최종 결제 요청 객체 생성
		return new OrderCreateRequest(
			orderItemRequests,
			totalProductPrice - request.usedPoints(),
			request.usedPoints(),
			request.recipient(),
			request.recipientPhone(),
			request.deliveryAddress(),
			request.detailAddress(),
			request.postalCode(),
			request.deliveryMessage()
		);
	}

}
