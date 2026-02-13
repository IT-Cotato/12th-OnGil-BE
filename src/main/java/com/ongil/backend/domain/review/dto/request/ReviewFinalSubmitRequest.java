package com.ongil.backend.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReviewFinalSubmitRequest {

    @Schema(description = "기타 후기")
    private String textReview;

    @Schema(description = "S3에서 받은 이미지 URL 리스트 (최대 5장)", example = "[\"https://s3.../1.jpg\"]")
    private List<String> reviewImageUrls;

    @Schema(description = "사이즈 관련 후기 문장 리스트", example = "[\"허리가 커서 걷다 보면 자꾸 흘러내려요\", \"신축성이 좋아 움직이기 편해요\"]")
    private List<String> sizeReview;

    @Schema(description = "소재 관련 후기 문장 리스트", example = "[\"소재가 부드러워 피부에 자극이 없어요\"]")
    private List<String> materialReview;
}
