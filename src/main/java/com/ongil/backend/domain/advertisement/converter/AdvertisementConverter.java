package com.ongil.backend.domain.advertisement.converter;

import com.ongil.backend.domain.advertisement.dto.AdvertisementResponse;
import org.springframework.stereotype.Component;

@Component
public class AdvertisementConverter {

    // 추후 Entity -> DTO 변환 시 사용
    public AdvertisementResponse toResponse(Long id, String title, String description, String imageUrl) {
        return AdvertisementResponse.builder()
                .id(id)
                .title(title)
                .description(description)
                .imageUrl(imageUrl)
                .build();
    }
}