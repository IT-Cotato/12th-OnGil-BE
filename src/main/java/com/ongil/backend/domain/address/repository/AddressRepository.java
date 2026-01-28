package com.ongil.backend.domain.address.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.address.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

	Optional<Address> findByUserId(Long userId);

	List<Address> findAllByUserId(Long userId);
}