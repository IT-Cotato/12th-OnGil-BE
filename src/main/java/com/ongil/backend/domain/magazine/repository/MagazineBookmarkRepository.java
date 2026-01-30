package com.ongil.backend.domain.magazine.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ongil.backend.domain.magazine.entity.Magazine;
import com.ongil.backend.domain.magazine.entity.MagazineBookmark;
import com.ongil.backend.domain.magazine.enums.MagazineCategory;
import com.ongil.backend.domain.user.entity.User;

@Repository
public interface MagazineBookmarkRepository extends JpaRepository<MagazineBookmark, Long> {
	Optional<MagazineBookmark> findByUserAndMagazine(User user, Magazine magazine);

	List<MagazineBookmark> findByUserOrderByCreatedAtDesc(User user);

	@Query("SELECT mb FROM MagazineBookmark mb JOIN mb.magazine m " +
		"WHERE mb.user = :user AND m.category = :category " +
		"ORDER BY mb.createdAt DESC")
	List<MagazineBookmark> findByUserAndMagazineCategoryOrderByCreatedAtDesc(
		@Param("user") User user,
		@Param("category") MagazineCategory category
	);
}
