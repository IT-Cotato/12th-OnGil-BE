package com.ongil.backend.domain.search.dto.openai;

import java.util.List;

public record OpenAiRequest(
	String model,
	List<Message> messages,
	double temperature
) {}
