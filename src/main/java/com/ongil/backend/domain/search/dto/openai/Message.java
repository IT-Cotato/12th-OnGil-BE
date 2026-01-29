package com.ongil.backend.domain.search.dto.openai;

public record Message(
	String role,
	String content
) {}