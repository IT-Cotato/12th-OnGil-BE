package com.ongil.backend.domain.review.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReviewStep1Response {

    private Long reviewId;
    private boolean needsSizeSecondaryQuestion;
    private String sizeSecondaryType;
    private boolean needsMaterialSecondaryQuestion;
    private String materialSecondaryType;
    private List<String> availableBodyParts;

    public static ReviewStep1Response of(Long reviewId, boolean needsSizeQ, String sizeSecondaryType,
        boolean needsMaterialQ, String materialSecondaryType, List<String> availableBodyParts) {
        return ReviewStep1Response.builder()
                .reviewId(reviewId)
                .needsSizeSecondaryQuestion(needsSizeQ)
                .sizeSecondaryType(sizeSecondaryType)
                .needsMaterialSecondaryQuestion(needsMaterialQ)
                .materialSecondaryType(materialSecondaryType)
                .availableBodyParts(availableBodyParts)
                .build();
    }
}
