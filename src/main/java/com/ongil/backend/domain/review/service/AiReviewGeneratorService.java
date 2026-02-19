package com.ongil.backend.domain.review.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ongil.backend.domain.review.dto.request.AiReviewGenerateRequest;
import com.ongil.backend.domain.review.dto.response.AiReviewResponse;
import com.ongil.backend.domain.review.service.prompter.MaterialReviewPrompter;
import com.ongil.backend.domain.review.service.prompter.SizeReviewPrompter;
import com.ongil.backend.domain.review.validator.ReviewValidator;
import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.openai.OpenAiClient;
import com.ongil.backend.global.openai.OpenAiException;
import com.ongil.backend.global.openai.OpenAiRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiReviewGeneratorService {

	private final OpenAiClient openAiClient;
	private final SizeReviewPrompter sizePrompter;
	private final MaterialReviewPrompter materialPrompter;
	private final ReviewValidator reviewValidator;

	public AiReviewResponse generateSizeReview(AiReviewGenerateRequest request) {
		reviewValidator.validateReviewStepCompletion(request.getSizeAnswer(), request.getFitIssueParts());

		try {
			String aiResponse = openAiClient.call(OpenAiRequest.of(
				"gpt-4o-mini",
				sizePrompter.getSystemPrompt(),
				sizePrompter.buildUserMessage(request),
				0.7
			));
			return AiReviewResponse.of(request.getReviewId(), parseAiResponse(aiResponse));
		} catch (OpenAiException e) {
			throw new AppException(ErrorCode.AI_GENERATION_ERROR);
		}
	}

	public AiReviewResponse generateMaterialReview(AiReviewGenerateRequest request) {
		reviewValidator.validateReviewStepCompletion(request.getMaterialAnswer(), request.getMaterialFeatures());

		try {
			String aiResponse = openAiClient.call(OpenAiRequest.of(
				"gpt-4o-mini",
				materialPrompter.getSystemPrompt(),
				materialPrompter.buildUserMessage(request),
				0.7
			));
			return AiReviewResponse.of(request.getReviewId(), parseAiResponse(aiResponse));
		} catch (OpenAiException e) {
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
