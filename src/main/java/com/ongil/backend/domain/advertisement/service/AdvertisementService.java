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
			advertisementConverter.toResponse(1L, "마지막 찬스 30% 쿠폰 드려요", "셀럽 전 상품 ~92% 할인", "[https://d3ha2047wt6x28.cloudfront.net/InlUyxK4zyo/pr:RECTANGLE_BANNER/czM6Ly9pbWcuYS1ibHkuY29tL2Jhbm5lci9pbWFnZXMvYmFubmVyX2ltYWdlXzE3NzA3MTAzNDcyOTU0MjcuanBn](https://d3ha2047wt6x28.cloudfront.net/InlUyxK4zyo/pr:RECTANGLE_BANNER/czM6Ly9pbWcuYS1ibHkuY29tL2Jhbm5lci9pbWFnZXMvYmFubmVyX2ltYWdlXzE3NzA3MTAzNDcyOTU0MjcuanBn)"),
			advertisementConverter.toResponse(2L, "연휴 직전 배송 찬스 12% 쿠폰 오늘 종료", "설맞이 오늘출발 셀럽 혜택", "[https://d3ha2047wt6x28.cloudfront.net/sMnRAE5QpJc/pr:RECTANGLE_BANNER/czM6Ly9pbWcuYS1ibHkuY29tL2Jhbm5lci9pbWFnZXMvYmFubmVyX2ltYWdlXzE3NzA2MjUwNzc4NDUwNzkucG5n](https://d3ha2047wt6x28.cloudfront.net/sMnRAE5QpJc/pr:RECTANGLE_BANNER/czM6Ly9pbWcuYS1ibHkuY29tL2Jhbm5lci9pbWFnZXMvYmFubmVyX2ltYWdlXzE3NzA2MjUwNzc4NDUwNzkucG5n)"),
			advertisementConverter.toResponse(3L, "스포츠 브랜드 BEST 스니커즈 대전", "아디다스/나이키/뉴발란스 외", "[https://d3ha2047wt6x28.cloudfront.net/whLHFPoO07k/pr:RECTANGLE_BANNER/czM6Ly9pbWcuYS1ibHkuY29tL2Jhbm5lci9pbWFnZXMvYmFubmVyX2ltYWdlXzE3NzAyOTQwNzgzOTIyMTUucG5n](https://d3ha2047wt6x28.cloudfront.net/whLHFPoO07k/pr:RECTANGLE_BANNER/czM6Ly9pbWcuYS1ibHkuY29tL2Jhbm5lci9pbWFnZXMvYmFubmVyX2ltYWdlXzE3NzAyOTQwNzgzOTIyMTUucG5n)"),
			advertisementConverter.toResponse(4L, "에잇세컨즈 클리어런스 ~71%", "8초 할인 어택[20% 쿠폰]까지!", "[https://d3ha2047wt6x28.cloudfront.net/AK_toMBUWpI/pr:RECTANGLE_BANNER/czM6Ly9pbWcuYS1ibHkuY29tL2Jhbm5lci9pbWFnZXMvYmFubmVyX2ltYWdlXzE3NzAyNzU5OTk4MDE4OTgucG5n](https://d3ha2047wt6x28.cloudfront.net/AK_toMBUWpI/pr:RECTANGLE_BANNER/czM6Ly9pbWcuYS1ibHkuY29tL2Jhbm5lci9pbWFnZXMvYmFubmVyX2ltYWdlXzE3NzAyNzU5OTk4MDE4OTgucG5n)"),
			advertisementConverter.toResponse(5L, "-50%할인 설 페스타", "본 행사는 한정수량으로 예고없이 조기 종료 될 수 있으며, 품복별 할인율은 상이 할 수 있습니다.", "[https://image11.coupangcdn.com/image/cmg/oms/banner/6e9af32c-3bc3-4b36-9223-125f1ea0f273_1080x650.jpg](https://image11.coupangcdn.com/image/cmg/oms/banner/6e9af32c-3bc3-4b36-9223-125f1ea0f273_1080x650.jpg)")
		);
	}
}