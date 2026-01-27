package com.ongil.backend.domain.mypagepopup.controller;

import com.ongil.backend.domain.mypagepopup.dto.response.MyPagePopupResponse;
import com.ongil.backend.domain.mypagepopup.service.MyPagePopupService;
import com.ongil.backend.global.common.dto.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypagepopups")
@Tag(name = "MyPagePopup", description = "마이페이지 팝업 관련 API")
public class MyPagePopupController {

    private final MyPagePopupService myPagePopupService;

    @Operation(summary = "마이페이지 팝업 목록 조회", description = "사용자의 마이페이지에 표시할 팝업 목록을 반환합니다.")
    @GetMapping
    public DataResponse<List<MyPagePopupResponse>> getMyPagePopups(
            @AuthenticationPrincipal Long userId
    ) {
        List<MyPagePopupResponse> result = myPagePopupService.getMyPagePopups(userId);
        return DataResponse.from(result);
    }
}
