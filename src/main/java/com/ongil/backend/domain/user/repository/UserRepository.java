package com.ongil.backend.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.auth.entity.LoginType;
import com.ongil.backend.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByLoginTypeAndLoginId(
		LoginType loginType,
		String socialId
	);

	boolean existsByLoginTypeAndLoginId(LoginType loginType, String loginId);
}