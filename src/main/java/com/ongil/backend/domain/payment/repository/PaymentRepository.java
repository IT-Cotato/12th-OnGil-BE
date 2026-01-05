package com.ongil.backend.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}