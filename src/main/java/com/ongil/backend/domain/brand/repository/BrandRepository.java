package com.ongil.backend.domain.brand.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.brand.entity.Brand;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}
