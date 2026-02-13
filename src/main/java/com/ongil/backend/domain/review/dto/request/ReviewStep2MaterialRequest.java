package com.ongil.backend.domain.review.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import com.ongil.backend.domain.review.enums.MaterialFeatureType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewStep2MaterialRequest {

    @NotEmpty(message = "소재 특징을 최소 1개 이상 선택해주세요.")
    private List<MaterialFeatureType> featureTypes;
}
