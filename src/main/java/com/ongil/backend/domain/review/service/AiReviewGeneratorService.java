package com.ongil.backend.domain.review.service;

import com.ongil.backend.domain.review.dto.request.AiReviewGenerateRequest;
import com.ongil.backend.domain.review.dto.response.AiReviewResponse;
import com.ongil.backend.domain.review.service.prompter.MaterialReviewPrompter;
import com.ongil.backend.domain.review.service.prompter.SizeReviewPrompter;
import com.ongil.backend.domain.review.validator.ReviewValidator;
import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiReviewGeneratorService {

    private final OpenAiService openAiService;
    private final SizeReviewPrompter sizePrompter;
    private final MaterialReviewPrompter materialPrompter;
    private final ReviewValidator reviewValidator;

    public AiReviewResponse generateSizeReview(AiReviewGenerateRequest request) {
        reviewValidator.validateReviewStepCompletion(request.getSizeAnswer(), request.getFitIssueParts());

        String systemPrompt = sizePrompter.getSystemPrompt();
        String userMessage = sizePrompter.buildUserMessage(request);

        String aiResponse = callOpenAi(systemPrompt, userMessage);
        return AiReviewResponse.of(request.getReviewId(), parseAiResponse(aiResponse));
    }

    public AiReviewResponse generateMaterialReview(AiReviewGenerateRequest request) {
        reviewValidator.validateReviewStepCompletion(request.getMaterialAnswer(), request.getMaterialFeatures());

        String systemPrompt = materialPrompter.getSystemPrompt();
        String userMessage = materialPrompter.buildUserMessage(request);

        String aiResponse = callOpenAi(systemPrompt, userMessage);
        return AiReviewResponse.of(request.getReviewId(), parseAiResponse(aiResponse));
    }

    private String callOpenAi(String systemPrompt, String userMessage) {
        List<ChatMessage> messages = List.of(
            new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt),
            new ChatMessage(ChatMessageRole.USER.value(), userMessage)
        );

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
            .model("gpt-4o-mini")
            .messages(messages)
            .temperature(0.7)
            .build();

        try {
            return openAiService.createChatCompletion(completionRequest)
                .getChoices().get(0).getMessage().getContent().trim();
        } catch (Exception e) {
            log.error("AI 생성 실패: ", e);
            throw new AppException(ErrorCode.AI_GENERATION_ERROR);
        }
    }

    private List<String> parseAiResponse(String aiResponse) {
        return Arrays.stream(aiResponse.split("\\|"))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();
    }
}
