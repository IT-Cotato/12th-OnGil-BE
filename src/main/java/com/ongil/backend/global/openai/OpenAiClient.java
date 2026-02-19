package com.ongil.backend.global.openai;

import java.util.List;

import org.springframework.stereotype.Component;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// OpenAI API 공통 호출 컴포넌트
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiClient {

	private final OpenAiService openAiService;

	public String call(OpenAiRequest request) {
		long startTime = System.currentTimeMillis();

		List<ChatMessage> messages = List.of(
			new ChatMessage(ChatMessageRole.SYSTEM.value(), request.systemPrompt()),
			new ChatMessage(ChatMessageRole.USER.value(), request.userMessage())
		);

		ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
			.model(request.model())
			.messages(messages)
			.temperature(request.temperature())
			.build();

		try {
			String result = openAiService.createChatCompletion(completionRequest)
				.getChoices().get(0).getMessage().getContent().trim();

			log.info("[OpenAI] 모델: {}, 소요시간: {}ms", request.model(), System.currentTimeMillis() - startTime);
			return result;

		} catch (Exception e) {
			log.error("[OpenAI] 호출 실패 - 모델: {}, 소요시간: {}ms", request.model(), System.currentTimeMillis() - startTime, e);
			throw new OpenAiException("OpenAI 호출 중 오류가 발생했습니다.", e);
		}
	}
}
