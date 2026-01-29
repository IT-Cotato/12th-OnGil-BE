package com.ongil.backend.domain.search.dto.openai;

import java.util.List;

public record OpenAiResponse(
	List<Choice> choices
) {
	public record Choice(
		Message message
	) {}
}
