package com.ongil.backend.domain.magazine.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ongil.backend.domain.magazine.entity.Magazine;
import com.ongil.backend.domain.magazine.enums.MagazineCategory;
import com.ongil.backend.domain.magazine.parser.MagazineArticleParser;
import com.ongil.backend.domain.magazine.repository.MagazineRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class MagazineCrawlingService {

	private final MagazineRepository magazineRepository;
	private final MagazineArticleParser articleParser;

	private static final int MAX_PAGES = 1;
	private static final int MAX_SAVE_PER_CATEGORY = 20;
	private static final String SEARCH_URL =
		"https://search.naver.com/search.naver?where=news&query=%s&sort=0&start=%d";

	public void crawlAndSave(MagazineCategory category) {
		List<String> searchQueries = getSearchQueriesByCategory(category);
		Set<String> visitedUrls = new HashSet<>();
		List<Magazine> candidates = new ArrayList<>();

		for (String searchQuery : searchQueries) {
			log.info("=== 크롤링 시작 | 카테고리: {} | 검색어: {} ===", category, searchQuery);
			for (int page = 1; page <= MAX_PAGES; page++) {

				int start = 1 + (page - 1) * 10;
				String url = String.format(
					SEARCH_URL,
					URLEncoder.encode(searchQuery, StandardCharsets.UTF_8),
					start
				);

				try {
					Document doc = Jsoup.connect(url)
						.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
						.timeout(10000)
						.get();

					List<String> articleLinks = extractArticleLinks(doc);

					for (String articleUrl : articleLinks) {
						if (!visitedUrls.add(articleUrl)) continue;

						articleParser.parse(articleUrl, category)
							.ifPresent(candidates::add);
					}

				} catch (IOException e) {
					log.warn("검색어 [{}] 페이지 {} 크롤링 실패", searchQuery, page, e);
				}
			}
		}

		Set<String> existUrls = magazineRepository.findAllUrlsByUrlIn(
			candidates.stream().map(Magazine::getUrl).toList()
		);

		List<Magazine> newMagazines = candidates.stream()
			.filter(m -> !existUrls.contains(m.getUrl()))
			.limit(MAX_SAVE_PER_CATEGORY)
			.toList();

		log.info("후보군 개수: {} | DB 중복 제외 후 신규 개수: {}", candidates.size(), newMagazines.size());
		magazineRepository.saveAll(newMagazines);

		log.info("=== 크롤링 종료 | 카테고리: {} | 저장 {}건 ===",
			category, newMagazines.size());
	}

	private List<String> getSearchQueriesByCategory(MagazineCategory category) {
		return switch (category) {
			case BODY_TYPE -> List.of("체형별 코디", "체형 보완 스타일링");
			case COLOR -> List.of("2026 패션 컬러", "트렌드 색상 스타일링");
			case MATERIAL -> List.of("의류 소재 특징", "패션 원단 트렌드");
			case PRICE -> List.of("가성비 패션", "아이템 세일");
		};
	}

	private List<String> extractArticleLinks(Document doc) {
		return doc.select("a[href]")
			.stream()
			.map(e -> e.attr("abs:href"))
			.filter(href -> href.contains("n.news.naver.com") && !href.contains("/comment/"))
			.distinct()
			.limit(20)
			.toList();
	}


}