package com.ongil.backend.domain.review.dto.request;

import com.ongil.backend.domain.review.enums.ClothingCategory;
import com.ongil.backend.domain.review.enums.ColorAnswer;
import com.ongil.backend.domain.review.enums.MaterialAnswer;
import com.ongil.backend.domain.review.enums.SizeAnswer;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewStep1Request {

    @NotBlank(message = "의류 카테고리는 필수입니다.")
    private ClothingCategory clothingCategory;

    @NotNull(message = "별점은 필수입니다.")
    @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "별점은 5점 이하여야 합니다.")
    private Integer rating;

    @NotBlank(message = "착용감 답변은 필수입니다.")
    private SizeAnswer sizeAnswer;

    @NotBlank(message = "색감 답변은 필수입니다.")
    private ColorAnswer colorAnswer;

    @NotBlank(message = "소재 답변은 필수입니다.")
    private MaterialAnswer materialAnswer;
}
