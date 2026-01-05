package com.ongil.backend.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}