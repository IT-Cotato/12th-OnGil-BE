package com.ongil.backend.domain.search.service;

import org.springframework.stereotype.Service;

import com.ongil.backend.global.openai.OpenAiClient;
import com.ongil.backend.global.openai.OpenAiException;
import com.ongil.backend.global.openai.OpenAiRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiSearchService {

	private final OpenAiClient openAiClient;

	public String extractKeywords(String speechText) {
		try {
			String result = openAiClient.call(OpenAiRequest.of(
				"gpt-4o-mini",
				"입력된 문장에서 검색용 핵심 키워드(용도, 브랜드, 품목)를 공백 구분으로 추출합니다.",
				buildUserMessage(speechText),
				0.1
			));
			String normalized = normalize(result);
			log.info("[AiSearchService] 추출 키워드: [{}] <- 원문: [{}]", normalized, speechText);
			return normalized;
		} catch (OpenAiException e) {
			log.warn("[AiSearchService] OpenAI 호출 실패, fallback 실행. 원문: {}", speechText);
			return fallbackExtract(speechText);
		}
	}

	private String buildUserMessage(String speechText) {
		return String.format("""
			[명령]
			다음 문장에서 쇼핑몰 상품 검색에 '직접적으로 필요한' 단어만 공백으로 구분해줘.
			
			[입력값]
			"%s"
			
			[처리 가이드]
			- '찾아줘', '보여줘'와 같은 요청어는 제외해.
			- 상품 종류(신발, 바지)뿐만 아니라 '용도(러닝, 축구, 요가)'나 '브랜드'도 중요 키워드로 추출해.
			- 추출된 단어들 사이에는 반드시 공백을 한 칸씩 넣어.
			- 오직 키워드만 출력해.
			
			[출력 예시]
			입력: "러닝할 때 신기 좋은 신발 찾아줘" -> 응답: 러닝 신발
			입력: "헬스장에서 입을 편한 바지" -> 응답: 헬스 바지
			입력: "나이키 축구화 보여줘" -> 응답: 나이키 축구화
			입력: "요즘 날씨에 신기 좋은 신발 찾아줘" -> 응답: 신발
			""", speechText);
	}

	private String normalize(String aiResult) {
		return aiResult
			.replaceAll("[,\\n]", " ")
			.replaceAll("\\s+", " ")
			.trim();
	}

	private String fallbackExtract(String text) {
		return text
			.replaceAll("(찾아줘|보여줘|추천해줘|추천)", "")
			.replaceAll("[^가-힣a-zA-Z0-9\\s]", "")
			.replaceAll("\\s+", " ")
			.trim();
	}
}
