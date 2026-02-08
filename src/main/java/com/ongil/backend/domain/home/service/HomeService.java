package com.ongil.backend.domain.home.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.advertisement.dto.AdvertisementResponse;
import com.ongil.backend.domain.advertisement.service.AdvertisementService;
import com.ongil.backend.domain.banner.dto.response.BannerResponse;
import com.ongil.backend.domain.banner.service.BannerService;
import com.ongil.backend.domain.home.converter.HomeConverter;
import com.ongil.backend.domain.home.dto.response.HomeResDto;
import com.ongil.backend.domain.magazine.dto.response.MagazineResDto;
import com.ongil.backend.domain.magazine.service.MagazineService;
import com.ongil.backend.domain.product.dto.response.RecommendedProductResponse;
import com.ongil.backend.domain.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final BannerService bannerService;
    private final AdvertisementService advertisementService;
    private final ProductService productService;
    private final MagazineService magazineService;

    private static final int RECOMMENDED_PRODUCTS_SIZE = 10;

    public HomeResDto getHomeData(Long userId) {
        // 1. Get personalized banner (review prompts, magazine recommendations)
        BannerResponse banner = bannerService.getBanner(userId);

        // 2. Get advertisements/promotions for home screen
        List<AdvertisementResponse> advertisements = advertisementService.getHomeAdvertisements();

        // 3. Get recommended products (personalized if logged in, popular if not)
        List<RecommendedProductResponse> recommendedProducts = 
                productService.getRecommendedProducts(userId, RECOMMENDED_PRODUCTS_SIZE);

        // 4. Get recommended magazines
        List<MagazineResDto> recommendedMagazines = magazineService.getRecommendedMagazines();

        // 5. Convert to HomeResDto
        return HomeConverter.toHomeResDto(banner, advertisements, recommendedProducts, recommendedMagazines);
    }
}