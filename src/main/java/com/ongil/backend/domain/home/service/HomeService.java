package com.ongil.backend.domain.home.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.home.converter.HomeConverter;
import com.ongil.backend.domain.home.dto.response.HomeResDto;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회 전용이므로 readOnly 권장
public class HomeService {

    // TODO: 나중에 ProductRepository, MagazineRepository 등을 여기에 DI 받습니다.

    public HomeResDto getHomeData() {
        // 1. DB에서 배너 가져오기 (가정)
        List<String> banners = List.of("https://banner1.com", "https://banner2.com");

        // 2. DB에서 추천 상품 가져오기 (가정)
        List<String> products = List.of("온길 시그니처 화분", "유기농 비료 세트");

        // 3. DB에서 최신 매거진 가져오기 (가정)
        String magazineTitle = "겨울철 식물 관리법";

        // 4. 음성 검색 및 장바구니 아이콘 URL (가정)
        String voiceSearchIconUrl = "https://example.com/icons/voice-search.svg";
        String cartIconUrl = "https://example.com/icons/cart.svg";

        // 5. Converter를 사용해 DTO로 변환
        return HomeConverter.toHomeResDto(banners, products, magazineTitle, voiceSearchIconUrl, cartIconUrl);
    }
}