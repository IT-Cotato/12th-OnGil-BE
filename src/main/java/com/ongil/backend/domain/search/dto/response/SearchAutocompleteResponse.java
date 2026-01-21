package com.ongil.backend.domain.search.dto.response;

import lombok.Builder;

@Builder
public record SearchAutocompleteResponse(
        Long id,
        String name,
        String type // "CATEGORY" or "BRAND"
) {}