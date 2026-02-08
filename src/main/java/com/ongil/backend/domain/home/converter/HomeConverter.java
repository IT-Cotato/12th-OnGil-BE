package com.ongil.backend.domain.home.converter;

import java.util.List;

import com.ongil.backend.domain.advertisement.dto.AdvertisementResponse;
import com.ongil.backend.domain.banner.dto.response.BannerResponse;
import com.ongil.backend.domain.home.dto.response.HomeResDto;
import com.ongil.backend.domain.magazine.dto.response.MagazineResDto;
import com.ongil.backend.domain.product.dto.response.RecommendedProductResponse;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HomeConverter {

    public static HomeResDto toHomeResDto(
            BannerResponse banner,
            List<AdvertisementResponse> advertisements,
            List<RecommendedProductResponse> recommendedProducts,
            List<MagazineResDto> recommendedMagazines
    ) {
        return HomeResDto.builder()
                .banner(banner)
                .advertisements(advertisements)
                .recommendedProducts(recommendedProducts)
                .recommendedMagazines(recommendedMagazines)
                .build();
    }
}