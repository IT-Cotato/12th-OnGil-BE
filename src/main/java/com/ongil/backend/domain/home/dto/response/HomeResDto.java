package com.ongil.backend.domain.home.dto.response;

import java.util.List;

import com.ongil.backend.domain.advertisement.dto.AdvertisementResponse;
import com.ongil.backend.domain.banner.dto.response.BannerResponse;
import com.ongil.backend.domain.magazine.dto.response.MagazineResDto;
import com.ongil.backend.domain.product.dto.response.RecommendedProductResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record HomeResDto(
        @Schema(description = "개인화 배너 (리뷰 작성 유도, 매거진 추천 등)")
        BannerResponse banner,

        @Schema(description = "홈 화면 광고 목록")
        List<AdvertisementResponse> advertisements,

        @Schema(description = "추천 상품 목록 (로그인 시 개인화, 비로그인 시 인기 상품)")
        List<RecommendedProductResponse> recommendedProducts,

        @Schema(description = "추천 매거진 목록")
        List<MagazineResDto> recommendedMagazines
) {}