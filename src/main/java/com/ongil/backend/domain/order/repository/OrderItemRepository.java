package com.ongil.backend.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.order.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}