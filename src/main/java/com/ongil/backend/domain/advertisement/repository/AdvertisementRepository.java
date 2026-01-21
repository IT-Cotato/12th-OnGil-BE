package com.ongil.backend.domain.advertisement.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ongil.backend.domain.advertisement.entity.Advertisement;
import com.ongil.backend.domain.advertisement.enums.AdvertisementType;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

	@Query("SELECT a FROM Advertisement a WHERE a.isActive = true " +
		"AND a.startDate <= :now AND a.endDate >= :now " +
		"ORDER BY a.displayOrder ASC")
	List<Advertisement> findActiveAdvertisements(@Param("now") LocalDateTime now);

	@Query("SELECT a FROM Advertisement a WHERE a.isActive = true " +
		"AND a.startDate <= :now AND a.endDate >= :now " +
		"AND a.advertisementType = :type " +
		"ORDER BY a.displayOrder ASC")
	List<Advertisement> findActiveAdvertisementsByType(
		@Param("now") LocalDateTime now,
		@Param("type") AdvertisementType type
	);

	@Query("SELECT a FROM Advertisement a WHERE a.isActive = true " +
		"AND a.startDate <= :now AND a.endDate >= :now " +
		"AND (a.targetCategory.id = :categoryId OR a.targetCategory.id IS NULL) " +
		"ORDER BY a.displayOrder ASC")
	List<Advertisement> findActiveAdvertisementsByCategory(
		@Param("now") LocalDateTime now,
		@Param("categoryId") Long categoryId
	);

	@Query("SELECT a FROM Advertisement a WHERE a.isActive = true " +
		"AND a.startDate <= :now AND a.endDate >= :now " +
		"AND (a.targetBrand.id = :brandId OR a.targetBrand.id IS NULL) " +
		"ORDER BY a.displayOrder ASC")
	List<Advertisement> findActiveAdvertisementsByBrand(
		@Param("now") LocalDateTime now,
		@Param("brandId") Long brandId
	);
}
