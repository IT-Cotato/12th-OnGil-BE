package com.ongil.backend.domain.home.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ongil.backend.domain.home.dto.response.HomeResDto;
import com.ongil.backend.domain.home.service.HomeService;
import com.ongil.backend.global.common.dto.DataResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Home", description = "홈 화면 관련 API (토큰 선택 - 로그인 시 개인화 데이터 제공)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
@Deprecated(since = "1.0", forRemoval = true)
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    @Operation(
            summary = "[Deprecated] 홈 화면 데이터 조회 API - 개별 API 사용 권장",
            description = """
                    ⚠️ 이 API는 deprecated 되었습니다. 대신 아래의 개별 API를 사용해주세요:
                    
                    1. 할인 광고 배너: GET /api/advertisements/home
                    2. 추천 상품 목록: GET /api/products/recommend?size=10
                    3. 추천 브랜드: GET /api/brands/recommend
                    4. 장바구니 개수: GET /api/carts/count (로그인 필요)
                    
                    개별 API를 사용하면 필요한 데이터만 선택적으로 조회할 수 있어 성능이 향상됩니다.
                    
                    ---
                    
                    [기존 기능] 홈 화면 구성에 필요한 모든 데이터를 한 번에 조회합니다.
                    - 할인 광고 배너 (5개)
                    - 추천 상품 목록 (로그인: 개인화 추천 / 비로그인: 인기 상품)
                    - 추천 브랜드 (랜덤 3개 브랜드 + 각 6개 상품)
                    - 장바구니 개수 (로그인 시에만, 비로그인 시 null)
                    """
    )
    @Deprecated(since = "1.0", forRemoval = true)
    public DataResponse<HomeResDto> getHomeData(
            @AuthenticationPrincipal Long userId
    ) {
        HomeResDto res = homeService.getHomeData(userId);
        return DataResponse.from(res);
    }
}
