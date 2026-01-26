package com.ongil.backend.domain.product.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

public record ProductSearchPageResDto(
	Page<ProductSimpleResponse> products,
	List<String> alternatives,
	boolean hasResult
) {
	public static ProductSearchPageResDto of(Page<ProductSimpleResponse> products, List<String> alternatives) {
		return new ProductSearchPageResDto(products, alternatives, !products.isEmpty());
	}
}