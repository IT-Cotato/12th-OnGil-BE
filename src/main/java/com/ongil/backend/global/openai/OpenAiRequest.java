package com.ongil.backend.global.openai;

public record OpenAiRequest(
	String model,
	String systemPrompt,
	String userMessage,
	double temperature
) {
	public static OpenAiRequest of(String model, String systemPrompt, String userMessage, double temperature) {
		return new OpenAiRequest(model, systemPrompt, userMessage, temperature);
	}
}
