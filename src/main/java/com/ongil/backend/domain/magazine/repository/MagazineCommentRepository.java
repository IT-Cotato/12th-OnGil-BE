package com.ongil.backend.domain.magazine.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.magazine.entity.MagazineComment;

public interface MagazineCommentRepository extends JpaRepository<MagazineComment, Long> {
}