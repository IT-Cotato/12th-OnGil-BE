package com.ongil.backend.domain.advertisement.dto;

import lombok.Builder;

@Builder
public record AdvertisementResponse(
        Long id,
        String imageUrl,
        String title,
        String description
) {
}