package com.ongil.backend.domain.home.converter;

import com.ongil.backend.domain.home.dto.response.HomeResDto;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class HomeConverter {

    // 나중에는 파라미터로 실제 Product, Magazine 객체를 받게 됩니다.
    public static HomeResDto toHomeResDto(List<String> banners, List<String> products, String magazineTitle) {
        return HomeResDto.builder()
                .bannerUrls(banners)
                .recommendProducts(products)
                .latestMagazineTitle(magazineTitle)
                .build();
    }
}