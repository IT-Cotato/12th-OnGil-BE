package com.ongil.backend.domain.advertisement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ongil.backend.domain.advertisement.entity.UserAdPreference;

public interface UserAdPreferenceRepository extends JpaRepository<UserAdPreference, Long> {

	@Query("SELECT p FROM UserAdPreference p WHERE p.user.id = :userId AND p.isInterested = true")
	List<UserAdPreference> findByUserIdAndIsInterestedTrue(@Param("userId") Long userId);

	@Query("SELECT p FROM UserAdPreference p WHERE p.user.id = :userId")
	List<UserAdPreference> findByUserId(@Param("userId") Long userId);

	@Query("SELECT p FROM UserAdPreference p WHERE p.user.id = :userId " +
		"AND (p.preferredCategory.id = :categoryId OR (:categoryId IS NULL AND p.preferredCategory IS NULL)) " +
		"AND (p.preferredBrand.id = :brandId OR (:brandId IS NULL AND p.preferredBrand IS NULL))")
	Optional<UserAdPreference> findByUserIdAndCategoryIdAndBrandId(
		@Param("userId") Long userId,
		@Param("categoryId") Long categoryId,
		@Param("brandId") Long brandId
	);
}
