package com.ongil.backend.domain.home.dto.response;

import java.util.List;

import com.ongil.backend.domain.advertisement.dto.AdvertisementResponse;
import com.ongil.backend.domain.brand.dto.response.BrandRecommendResponse;
import com.ongil.backend.domain.product.dto.response.RecommendedProductResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "[Deprecated] 홈 화면 통합 응답 - 개별 API 사용 권장")
@Deprecated(since = "1.0", forRemoval = true)
public record HomeResDto(

        @Schema(description = "할인 광고 배너 목록 (5개)")
        List<AdvertisementResponse> advertisements,

        @Schema(description = "추천 상품 목록")
        List<RecommendedProductResponse> recommendedProducts,

        @Schema(description = "추천 브랜드 목록 (3개 브랜드 + 각 6개 상품)")
        List<BrandRecommendResponse> recommendBrands,

        @Schema(description = "장바구니 담긴 개수 (비로그인 시 null)")
        Long cartCount
) {}
