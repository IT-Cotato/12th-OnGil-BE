package com.ongil.backend.domain.search.dto.response;

import lombok.Builder;

@Builder
public record SearchLogResponse(
        String keyword
) {}