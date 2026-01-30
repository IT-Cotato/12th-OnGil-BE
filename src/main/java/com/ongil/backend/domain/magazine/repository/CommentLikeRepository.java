package com.ongil.backend.domain.magazine.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ongil.backend.domain.magazine.entity.MagazineComment;
import com.ongil.backend.domain.magazine.entity.MagazineCommentLike;
import com.ongil.backend.domain.user.entity.User;

@Repository
public interface CommentLikeRepository extends JpaRepository<MagazineCommentLike, Long> {
	Optional<MagazineCommentLike> findByCommentAndUser(MagazineComment comment, User user);
}
