package com.ongil.backend.domain.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}