package com.ongil.backend.domain.review.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import com.ongil.backend.domain.review.enums.ClothingCategory;
import com.ongil.backend.domain.review.enums.MaterialAnswer;
import com.ongil.backend.domain.review.enums.SizeAnswer;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AiReviewGenerateRequest {

    private Long reviewId;

    private ClothingCategory clothingType;
    private SizeAnswer sizeAnswer;
    private MaterialAnswer materialAnswer;

    private List<String> fitIssueParts;

    private List<String> materialFeatures;
}
