package com.ongil.backend.domain.magazine.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ongil.backend.domain.magazine.enums.MagazineCategory;
import com.ongil.backend.domain.magazine.service.MagazineCrawlingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MagazineScheduler {

	private final MagazineCrawlingService magazineCrawlingService;

	/**
	 * 매주 월요일 새벽 3시
	 */
	@Scheduled(cron = "0 0 3 ? * MON")
	public void weeklyCrawlMagazines() {
		log.info("매거진 주간 크롤링 시작");
		crawlAll();
		log.info("매거진 주간 크롤링 완료");
	}

	private void crawlAll() {
		for (MagazineCategory category : MagazineCategory.values()) {
			try {
				magazineCrawlingService.crawlAndSave(category);
			} catch (Exception e) {
				log.error("카테고리 크롤링 실패: {}", category, e);
			}
		}
	}
}
