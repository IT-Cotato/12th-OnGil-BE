package com.ongil.backend.domain.home.converter;

import java.util.List;

import com.ongil.backend.domain.advertisement.dto.AdvertisementResponse;
import com.ongil.backend.domain.brand.dto.response.BrandRecommendResponse;
import com.ongil.backend.domain.home.dto.response.HomeResDto;
import com.ongil.backend.domain.product.dto.response.RecommendedProductResponse;

import lombok.experimental.UtilityClass;

@UtilityClass
@Deprecated(since = "1.0", forRemoval = true)
public class HomeConverter {

    /**
     * @deprecated 개별 API 사용 권장
     */
    @Deprecated(since = "1.0", forRemoval = true)
    public static HomeResDto toHomeResDto(
            List<AdvertisementResponse> advertisements,
            List<RecommendedProductResponse> recommendedProducts,
            List<BrandRecommendResponse> recommendBrands,
            Long cartCount
    ) {
        return HomeResDto.builder()
                .advertisements(advertisements)
                .recommendedProducts(recommendedProducts)
                .recommendBrands(recommendBrands)
                .cartCount(cartCount)
                .build();
    }
}
