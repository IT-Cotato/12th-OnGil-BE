package com.ongil.backend.domain.magazine.repository;

import java.util.List;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ongil.backend.domain.magazine.entity.MagazineComment;

@Repository
public interface MagazineCommentRepository extends JpaRepository<MagazineComment, Long> {


	@Query("SELECT mc FROM MagazineComment mc JOIN FETCH mc.user " +
		"WHERE mc.magazine.id = :magazineId " +
		"ORDER BY mc.createdAt DESC")
	List<MagazineComment> findByMagazineIdOrderByCreatedAtDesc(@Param("magazineId") Long magazineId);
}