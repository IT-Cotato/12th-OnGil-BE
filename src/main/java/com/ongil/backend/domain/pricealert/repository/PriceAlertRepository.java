package com.ongil.backend.domain.pricealert.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.pricealert.entity.PriceAlert;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, Long> {
}