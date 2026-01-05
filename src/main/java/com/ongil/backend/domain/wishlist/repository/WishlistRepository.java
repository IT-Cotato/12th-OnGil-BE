package com.ongil.backend.domain.wishlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.wishlist.entity.Wishlist;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
}