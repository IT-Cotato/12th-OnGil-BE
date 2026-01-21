package com.ongil.backend.domain.home.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.advertisement.dto.response.AdvertisementResponse;
import com.ongil.backend.domain.advertisement.service.AdvertisementService;
import com.ongil.backend.domain.home.converter.HomeConverter;
import com.ongil.backend.domain.home.dto.response.HomeResDto;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 전용이므로 readOnly 권장
public class HomeService {

    private final AdvertisementService advertisementService;

    public HomeResDto getHomeData() {
        // 1. DB에서 활성 광고 가져오기
        List<AdvertisementResponse> advertisements = advertisementService.getActiveAdvertisements();
        List<String> banners = advertisements.stream()
            .map(AdvertisementResponse::getImageUrl)
            .collect(Collectors.toList());

        // 2. DB에서 추천 상품 가져오기 (가정)
        List<String> products = List.of("온길 시그니처 화분", "유기농 비료 세트");

        // 3. DB에서 최신 매거진 가져오기 (가정)
        String magazineTitle = "겨울철 식물 관리법";

        // 4. Converter를 사용해 DTO로 변환
        return HomeConverter.toHomeResDto(banners, products, magazineTitle);
    }

    public HomeResDto getPersonalizedHomeData(Long userId) {
        // 1. 사용자 맞춤 광고 가져오기
        List<AdvertisementResponse> advertisements = advertisementService.getPersonalizedAdvertisements(userId);
        List<String> banners = advertisements.stream()
            .map(AdvertisementResponse::getImageUrl)
            .collect(Collectors.toList());

        // 2. DB에서 추천 상품 가져오기 (가정)
        List<String> products = List.of("온길 시그니처 화분", "유기농 비료 세트");

        // 3. DB에서 최신 매거진 가져오기 (가정)
        String magazineTitle = "겨울철 식물 관리법";

        // 4. Converter를 사용해 DTO로 변환
        return HomeConverter.toHomeResDto(banners, products, magazineTitle);
    }
}