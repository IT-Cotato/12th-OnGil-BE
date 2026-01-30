package com.ongil.backend.domain.order.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

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
		String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String randomPart = UUID.randomUUID().toString().substring(0, 8);
		String orderNumber = "ORD-" + datePart + "-" + randomPart;

		return Order.builder()
			.orderNumber(orderNumber)
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
	public OrderItem toOrderItem(OrderItemRequest itemRequest, Product product) {
		return OrderItem.builder()
			.product(product)
			.priceAtOrder(product.getPrice())
			.selectedSize(itemRequest.selectedSize())
			.selectedColor(itemRequest.selectedColor())
			.quantity(itemRequest.quantity())
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
		List<OrderItemRequest> orderItemRequests = cartItems.stream()
			.map(cartItem -> new OrderItemRequest(
				cartItem.getProduct().getId(),
				cartItem.getSelectedSize(),
				cartItem.getSelectedColor(),
				cartItem.getQuantity()
			))
			.toList();

		return new OrderCreateRequest(
			orderItemRequests,
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
