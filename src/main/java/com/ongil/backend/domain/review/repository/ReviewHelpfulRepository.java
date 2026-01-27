package com.ongil.backend.domain.review.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ongil.backend.domain.review.entity.ReviewHelpful;

public interface ReviewHelpfulRepository extends JpaRepository<ReviewHelpful, Long> {

	Optional<ReviewHelpful> findByReviewIdAndUserId(Long reviewId, Long userId);

	// 리뷰에 대해 특정 사용자가 도움돼요를 눌렀는지 여부 확인
	boolean existsByReviewIdAndUserId(Long reviewId, Long userId);

	// 리뷰 도움돼요 기록 삭제
	void deleteByReviewIdAndUserId(Long reviewId, Long userId);
}
