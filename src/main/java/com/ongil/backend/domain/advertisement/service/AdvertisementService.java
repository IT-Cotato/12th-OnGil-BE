package com.ongil.backend.domain.advertisement.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.advertisement.converter.AdvertisementConverter;
import com.ongil.backend.domain.advertisement.dto.AdvertisementResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvertisementService {

	private final AdvertisementConverter advertisementConverter;

	// 메인 홈 화면 할인 광고 목록 조회 (Mock Data)
	public List<AdvertisementResponse> getHomeAdvertisements() {
		// 실제 DB 조회 대신 목데이터 5개 생성
		return List.of(
			advertisementConverter.toResponse(1L, "마지막 찬스 30% 쿠폰 드려요", "셀럽 전 상품 ~92% 할인", "https://ongil-bucket.s3.ap-northeast-2.amazonaws.com/advertisement/ad1.png"),
			advertisementConverter.toResponse(2L, "연휴 직전 배송 찬스 12% 쿠폰 오늘 종료", "설맞이 오늘출발 셀럽 혜택", "https://ongil-bucket.s3.ap-northeast-2.amazonaws.com/advertisement/ad2.png"),
			advertisementConverter.toResponse(3L, "스포츠 브랜드 BEST 스니커즈 대전", "아디다스/나이키/뉴발란스 외", "https://ongil-bucket.s3.ap-northeast-2.amazonaws.com/advertisement/ad3.png"),
			advertisementConverter.toResponse(4L, "에잇세컨즈 클리어런스 ~71%", "8초 할인 어택[20% 쿠폰]까지!", "https://ongil-bucket.s3.ap-northeast-2.amazonaws.com/advertisement/ad4.png"),
			advertisementConverter.toResponse(5L, "갓성비 아이템 클리어런스! 금주의 ~70% 특가왕", "본 행사는 한정수량으로 예고없이 조기 종료 될 수 있으며, 품복별 할인율은 상이 할 수 있습니다.", "https://ongil-bucket.s3.ap-northeast-2.amazonaws.com/advertisement/ad5.png")
		);
	}
}