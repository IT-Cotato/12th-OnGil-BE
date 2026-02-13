package com.ongil.backend.domain.review.service.prompter;

import com.ongil.backend.domain.review.dto.request.AiReviewGenerateRequest;

public interface ReviewPrompter {
	String getSystemPrompt();
	String buildUserMessage(AiReviewGenerateRequest request);
}
