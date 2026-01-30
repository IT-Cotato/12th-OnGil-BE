package com.ongil.backend.domain.magazine.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.ongil.backend.domain.magazine.entity.Magazine;
import com.ongil.backend.domain.magazine.enums.MagazineCategory;

@Component
@Slf4j
public class NaverNewsParser implements MagazineArticleParser {

	@Override
	public Optional<Magazine> parse(String url, MagazineCategory category) {
		try {
			Thread.sleep(800 + (long)(Math.random() * 500)); // 랜덤 지연

			Document doc = Jsoup.connect(url)
				.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
				.timeout(10000)
				.get();

			String title = extractTitle(doc);
			String content = extractContent(doc);

			if (title.isEmpty() || content.isEmpty()) {
				log.warn("제목이나 본문이 비어있음: {}", url);
				return Optional.empty();
			}

			return Optional.of(
				Magazine.builder()
					.title(title)
					.content(content)
					.url(url)
					.category(category)
					.press(extractPress(doc))
					.thumbnailImageUrl(extractThumbnail(doc))
					.authorName(extractAuthor(doc))
					.publishedAt(extractPublishedAt(doc))
					.viewCount(0)
					.build()
			);
		} catch (Exception e) {
			log.warn("기사 파싱 실패: {}", url, e);
			return Optional.empty();
		}
	}

	private String extractThumbnail(Document doc) {
		Element metaOgImage = doc.selectFirst("meta[property=og:image]");
		return metaOgImage != null ? metaOgImage.attr("content") : null;
	}

	private String extractPress(Document doc) {
		Element pressEl = doc.selectFirst(".media_end_head_top_logo img");
		if (pressEl != null) return pressEl.attr("alt");

		Element pressTextEl = doc.selectFirst(".media_end_head_top_channel_layer_text");
		return pressTextEl != null ? pressTextEl.text().trim() : "알 수 없음";
	}

	private String extractAuthor(Document doc) {
		Element authorEl = doc.selectFirst(".byline_s, .media_end_head_journalist_name");
		if (authorEl != null) {
			return authorEl.text().trim();
		}
		return null;
	}

	private String extractTitle(Document doc) {
		Element titleEl = doc.selectFirst("#title_area, h2.media_end_head_headline, #articleTitle");
		return titleEl != null ? titleEl.text().trim() : "";
	}

	private String extractContent(Document doc) {
		Element body = doc.selectFirst("#newsct_article, #dic_area, #articleBodyContents");

		if (body != null) {
			body.select("script, style, .end_photo_org, .article_footer, .reporter_area, .copyright").remove();
			return body.text().trim();
		}
		return "";
	}

	private static final DateTimeFormatter NAVER_DATE_FORMATTER =
		DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private LocalDateTime extractPublishedAt(Document doc) {
		try {
			Element time = doc.selectFirst("span[data-date-time]");
			if (time != null) {
				String dateStr = time.attr("data-date-time"); // "2026-01-30 11:39:40"
				return LocalDateTime.parse(dateStr, NAVER_DATE_FORMATTER);
			}

			Element meta = doc.selectFirst("meta[property=article:published_time]");
			if (meta != null) {
				String content = meta.attr("content");
				return LocalDateTime.parse(content.replace(" ", "T"),
					DateTimeFormatter.ISO_OFFSET_DATE_TIME);
			}
		} catch (Exception e) {
			log.warn("날짜 파싱 중 오류 발생, 현재 시간으로 대체합니다.");
		}
		return LocalDateTime.now();
	}


}

