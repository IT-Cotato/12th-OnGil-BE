package com.ongil.backend.domain.review.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AiReviewResponse {

    private Long reviewId;

    private List<String> aiGeneratedReviews;
    
    public static AiReviewResponse of(Long reviewId, List<String> aiReviews) {
        return AiReviewResponse.builder()
                .reviewId(reviewId)
                .aiGeneratedReviews(aiReviews)
                .build();
    }
}
