package com.ongil.backend.domain.search.dto.response;

import java.util.List;

import com.ongil.backend.domain.search.document.ProductDocument;

public record SearchResDto(
	List<ProductDocument> products,
	List<String> alternatives
) {
	public static SearchResDto of(List<ProductDocument> products, List<String> alternatives) {
		return new SearchResDto(products, alternatives);
	}
}

