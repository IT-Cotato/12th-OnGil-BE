package com.ongil.backend.domain.home.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    @Operation(summary = "홈 화면 데이터 조회 API", description = "메인 배너, 추천 상품 등 홈 화면 구성을 위한 데이터를 조회합니다.")
    public ResponseEntity<DataResponse<HomeResDto>> getHomeData() {
        HomeResDto res = homeService.getHomeData();
        return ResponseEntity.ok(DataResponse.from(res));
    }

    @GetMapping("/personalized")
    @Operation(summary = "사용자 맞춤 홈 화면 데이터 조회 API", description = "사용자 선호도에 맞춘 메인 배너, 추천 상품 등을 조회합니다.")
    public ResponseEntity<DataResponse<HomeResDto>> getPersonalizedHomeData(@RequestParam Long userId) {
        HomeResDto res = homeService.getPersonalizedHomeData(userId);
        return ResponseEntity.ok(DataResponse.from(res));
    }
}