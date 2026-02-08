package com.ongil.backend.domain.home.controller;

import org.springframework.http.ResponseEntity;
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

@Tag(name = "Home", description = "홈 화면 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    @Operation(summary = "홈 화면 데이터 조회 API", description = """
        홈 화면 구성을 위한 데이터를 조회합니다.
        - 개인화 배너 (리뷰 작성 유도, 매거진 추천 등)
        - 홈 화면 광고 목록
        - 추천 상품 (로그인 시 개인화, 비로그인 시 인기 상품)
        - 추천 매거진
        
        로그인 사용자의 경우 개인화된 추천을 제공합니다.
        """)
    public ResponseEntity<DataResponse<HomeResDto>> getHomeData(
            @AuthenticationPrincipal Long userId
    ) {
        HomeResDto res = homeService.getHomeData(userId);
        return ResponseEntity.ok(DataResponse.from(res));
    }
}