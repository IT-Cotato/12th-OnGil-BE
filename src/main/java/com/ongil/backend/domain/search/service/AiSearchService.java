package com.ongil.backend.domain.search.service;

import com.ongil.backend.domain.search.dto.openai.Message;
import com.ongil.backend.domain.search.dto.openai.OpenAiRequest;
import com.ongil.backend.domain.search.dto.openai.OpenAiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiSearchService {

	@Value("${openai.api-key}")
	private String apiKey;

	private final RestTemplate restTemplate;

	private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
	private static final String MODEL = "gpt-4o-mini";

	public String extractKeywords(String speechText) {
		long startTime = System.currentTimeMillis();
		try {
			OpenAiRequest request = createRequest(speechText);

			OpenAiResponse response = restTemplate.postForObject(
				OPENAI_URL,
				createEntity(request),
				OpenAiResponse.class
			);

			long endTime = System.currentTimeMillis();
			log.info("AI 모델: {}, 소요 시간: {}ms", MODEL, (endTime - startTime));

			if (response == null || response.choices() == null || response.choices().isEmpty()) {
				return fallbackExtract(speechText);
			}

			OpenAiResponse.Choice firstChoice = response.choices().get(0);
			if (firstChoice.message() == null || firstChoice.message().content() == null) {
				log.warn("OpenAI 응답 본문이 비어있습니다. fallback을 실행합니다.");
				return fallbackExtract(speechText);
			}

			String result = normalize(firstChoice.message().content());
			log.info("추출 키워드: [{}] <- 원문: [{}]", result, speechText);
			return result;

		} catch (Exception e) {
			log.warn("OpenAI 호출 실패 ({}ms 소요). fallback 실행", (System.currentTimeMillis() - startTime), e);
			return fallbackExtract(speechText);
		}
	}

	private OpenAiRequest createRequest(String speechText) {
		String prompt = String.format("""
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

		return new OpenAiRequest(MODEL, List.of(
			new Message("system", "입력된 문장에서 검색용 핵심 키워드(용도, 브랜드, 품목)를 공백 구분으로 추출합니다."),
			new Message("user", prompt)
		), 0.1);
	}

	private HttpEntity<OpenAiRequest> createEntity(OpenAiRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(apiKey);
		return new HttpEntity<>(request, headers);
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