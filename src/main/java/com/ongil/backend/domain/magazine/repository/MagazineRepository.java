package com.ongil.backend.domain.magazine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.magazine.entity.Magazine;

public interface MagazineRepository extends JpaRepository<Magazine, Long> {
}