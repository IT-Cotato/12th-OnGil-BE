package com.ongil.backend.domain.review.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewStep1Response {

    private Long reviewId;
    private boolean needsSizeSecondaryQuestion;
    private boolean needsMaterialSecondaryQuestion;
    private List<String> availableBodyParts;
    
    public static ReviewStep1Response of(Long reviewId, boolean needsSizeQ, boolean needsMaterialQ, List<String> availableBodyParts) {
        return ReviewStep1Response.builder()
                .reviewId(reviewId)
                .needsSizeSecondaryQuestion(needsSizeQ)
                .needsMaterialSecondaryQuestion(needsMaterialQ)
                .availableBodyParts(availableBodyParts)
                .build();
    }
}
