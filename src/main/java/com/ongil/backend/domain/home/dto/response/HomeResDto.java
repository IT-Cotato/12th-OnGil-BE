package com.ongil.backend.domain.home.dto.response;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record HomeResDto(
        @Schema(description = "메인 배너 이미지 URL 리스트")
        List<String> bannerUrls,

        @Schema(description = "추천 상품 이름 리스트 (예시)")
        List<String> recommendProducts,

        @Schema(description = "최신 매거진 제목")
        String latestMagazineTitle
) {}