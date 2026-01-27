package com.ongil.backend.domain.mypagepopup.service;

import com.ongil.backend.domain.mypagepopup.converter.MyPagePopupConverter;
import com.ongil.backend.domain.mypagepopup.dto.response.MyPagePopupResponse;
import com.ongil.backend.domain.mypagepopup.repository.MyPagePopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPagePopupService {

    private final MyPagePopupRepository myPagePopupRepository;
    private final MyPagePopupConverter myPagePopupConverter;

    // 마이페이지 팝업 목록 조회 (Mock Data)
    public List<MyPagePopupResponse> getMyPagePopups(Long userId) {
        // 실제 DB 조회 대신 목데이터 생성
        // 추후 실제 DB 연동 시:
        // LocalDateTime now = LocalDateTime.now();
        // String userType = determineUserType(userId); // 사용자 타입 결정 로직
        // List<MyPagePopup> popups = myPagePopupRepository.findActivePopups(now, userType);
        // return popups.stream().map(myPagePopupConverter::toResponse).toList();
        
        return List.of(
                myPagePopupConverter.toResponse(
                        1L,
                        "신규 회원 환영 혜택",
                        "회원가입을 축하드립니다! 첫 구매 시 10% 할인 쿠폰을 드립니다.",
                        "https://example.com/images/popup1.jpg",
                        "/promotion/welcome",
                        100
                ),
                myPagePopupConverter.toResponse(
                        2L,
                        "포인트 적립 안내",
                        "현재 보유 포인트를 확인하고 다양한 혜택을 받아보세요!",
                        "https://example.com/images/popup2.jpg",
                        "/mypage/points",
                        90
                ),
                myPagePopupConverter.toResponse(
                        3L,
                        "프로필 업데이트 요청",
                        "사이즈 정보를 등록하면 맞춤형 상품을 추천해드립니다.",
                        "https://example.com/images/popup3.jpg",
                        "/mypage/profile",
                        80
                )
        );
    }
}
