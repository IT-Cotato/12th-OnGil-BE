package com.ongil.backend.domain.home.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.advertisement.dto.AdvertisementResponse;
import com.ongil.backend.domain.advertisement.service.AdvertisementService;
import com.ongil.backend.domain.brand.dto.response.BrandRecommendResponse;
import com.ongil.backend.domain.brand.service.BrandService;
import com.ongil.backend.domain.cart.service.CartService;
import com.ongil.backend.domain.home.converter.HomeConverter;
import com.ongil.backend.domain.home.dto.response.HomeResDto;
import com.ongil.backend.domain.product.dto.response.RecommendedProductResponse;
import com.ongil.backend.domain.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private static final int RECOMMENDED_PRODUCTS_SIZE = 10;

    private final AdvertisementService advertisementService;
    private final ProductService productService;
    private final BrandService brandService;
    private final CartService cartService;

    public HomeResDto getHomeData(Long userId) {
        List<AdvertisementResponse> advertisements = advertisementService.getHomeAdvertisements();
        List<RecommendedProductResponse> recommendedProducts = productService.getRecommendedProducts(userId, RECOMMENDED_PRODUCTS_SIZE);
        List<BrandRecommendResponse> recommendBrands = brandService.getRecommendBrands();
        Long cartCount = (userId != null) ? cartService.getCartCount(userId) : null;

        return HomeConverter.toHomeResDto(advertisements, recommendedProducts, recommendBrands, cartCount);
    }
}
