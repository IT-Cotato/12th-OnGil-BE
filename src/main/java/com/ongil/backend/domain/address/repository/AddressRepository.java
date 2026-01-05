package com.ongil.backend.domain.address.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.address.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}