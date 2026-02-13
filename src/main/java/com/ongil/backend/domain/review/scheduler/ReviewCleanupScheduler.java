package com.ongil.backend.domain.review.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewCleanupScheduler {

	private final ReviewRepository reviewRepository;

	// 30분 간격
	@Transactional
	@Scheduled(fixedRate = 1800000)
	public void cleanupDraftReviews() {
		log.info("임시 저장 리뷰 정리 스케줄러 실행");

		LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);

		try {
			reviewRepository.deleteExpiredDraftReviews(threshold);
			log.info("만료된 DRAFT 리뷰 삭제 완료 (기준 시간: {})", threshold);
		} catch (Exception e) {
			log.error("DRAFT 리뷰 정리 중 에러 발생: ", e);
		}
	}
}
