package com.ongil.backend.domain.search.dto.response;

import com.ongil.backend.domain.product.dto.response.ProductSearchPageResDto;

public record VoiceSearchResDto(
	String extractedKeyword,
	ProductSearchPageResDto searchResult
) {}