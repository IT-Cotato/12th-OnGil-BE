package com.ongil.backend.domain.mypagepopup.converter;

import com.ongil.backend.domain.mypagepopup.dto.response.MyPagePopupResponse;
import com.ongil.backend.domain.mypagepopup.entity.MyPagePopup;
import org.springframework.stereotype.Component;

@Component
public class MyPagePopupConverter {

    public MyPagePopupResponse toResponse(MyPagePopup entity) {
        return MyPagePopupResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .actionUrl(entity.getActionUrl())
                .priority(entity.getPriority())
                .build();
    }

    // Temporary method for creating response from mock data
    public MyPagePopupResponse toResponse(Long id, String title, String description, String imageUrl, String actionUrl, Integer priority) {
        return MyPagePopupResponse.builder()
                .id(id)
                .title(title)
                .description(description)
                .imageUrl(imageUrl)
                .actionUrl(actionUrl)
                .priority(priority)
                .build();
    }
}
