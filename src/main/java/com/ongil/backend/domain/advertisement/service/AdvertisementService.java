package com.ongil.backend.domain.advertisement.service;

import com.ongil.backend.domain.advertisement.converter.AdvertisementConverter;
import com.ongil.backend.domain.advertisement.dto.AdvertisementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvertisementService {

	private final AdvertisementConverter advertisementConverter;

	// 메인 홈 화면 할인 광고 목록 조회 (Mock Data)
	public List<AdvertisementResponse> getHomeAdvertisements() {
		// 실제 DB 조회 대신 목데이터 5개 생성
		return List.of(
			advertisementConverter.toResponse(1L, "온길 새로운 파격 혜택", "홈쇼핑이 막막한 당신에게!", "https://example.com/images/ad1.jpg"),
			advertisementConverter.toResponse(2L, "봄맞이 효도 상품 기획전", "부모님께 사랑을 전하세요", "https://example.com/images/ad2.jpg"),
			advertisementConverter.toResponse(3L, "주말 한정 특가", "놓치면 후회할 베스트 아이템", "https://example.com/images/ad3.jpg"),
			advertisementConverter.toResponse(4L, "신규 회원 웰컴 쿠폰", "가입 즉시 10,000원 할인", "https://example.com/images/ad4.jpg"),
			advertisementConverter.toResponse(5L, "이달의 추천 카테고리", "가장 인기 있는 상품 모음", "https://example.com/images/ad5.jpg")
		);
	}
}