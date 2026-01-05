package com.ongil.backend.domain.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.auth.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
