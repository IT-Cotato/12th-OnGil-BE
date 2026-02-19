package com.ongil.backend.domain.product.service;

import org.springframework.stereotype.Service;

import com.ongil.backend.domain.product.dto.response.AiMaterialDescriptionResponse;
import com.ongil.backend.global.openai.OpenAiClient;
import com.ongil.backend.global.openai.OpenAiException;
import com.ongil.backend.global.openai.OpenAiRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiMaterialService {

	private final OpenAiClient openAiClient;

	public AiMaterialDescriptionResponse generate(String material) {
		try {
			OpenAiRequest request = OpenAiRequest.of(
				"gpt-3.5-turbo",
				"당신은 의류 소재 전문가입니다. 60대 이상 어르신이 이해하기 쉽게 설명해주세요.",
				createPrompt(material),
				0.3
			);
			String response = openAiClient.call(request);
			return parseResponse(response);
		} catch (OpenAiException e) {
			log.warn("[AiMaterialService] AI 호출 실패, 기본값 반환. 소재: {}", material);
			return AiMaterialDescriptionResponse.createDefault();
		} catch (Exception e) {
			log.warn("[AiMaterialService] 응답 파싱 실패, 기본값 반환. 소재: {}", material);
			return AiMaterialDescriptionResponse.createDefault();
		}
	}

	private String createPrompt(String material) {
		return String.format("""
			다음 의류 소재에 대해 설명해주세요.
			
			소재: %s
			
			아래 형식으로 정확히 작성해주세요:
			
			[장점]
			장점1
			장점2
			장점3
			장점4
			
			[단점]
			단점1
			단점2
			단점3
			
			[세탁방법]
			세탁1
			세탁2
			세탁3
			세탁4
			
			조건:
			- 각 섹션당 최대 4개 문장
			- 한 문장은 공백 포함 최대 16자
			- 16자를 초과하면 반드시 다시 줄여서 수정
			- 16자를 초과한 문장은 출력하지 말 것
			- 줄바꿈 없이 한 문장으로 작성
			- 전문 용어, 추상적 표현 사용 금지
			- 초등학생도 이해할 수 있는 쉬운 말만 사용
			- 말하듯 자연스러운 '~해요' 말투 사용
			""", material);
	}

	private AiMaterialDescriptionResponse parseResponse(String response) {
		String[] sections = response.split("\\[장점\\]|\\[단점\\]|\\[세탁방법\\]");

		if (sections.length < 4) {
			return AiMaterialDescriptionResponse.createDefault();
		}

		return AiMaterialDescriptionResponse.builder()
			.advantages(cleanSection(sections[1]))
			.disadvantages(cleanSection(sections[2]))
			.care(cleanSection(sections[3]))
			.build();
	}

	private String cleanSection(String section) {
		return section.trim()
			.replaceAll("^-\\s*", "")
			.replaceAll("\n-\\s*", "\n")
			.trim();
	}
}
