package com.ongil.backend.domain.pricealert.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.pricealert.converter.PriceAlertConverter;
import com.ongil.backend.domain.pricealert.dto.request.PriceAlertRequest;
import com.ongil.backend.domain.pricealert.dto.response.PriceAlertResponse;
import com.ongil.backend.domain.pricealert.entity.PriceAlert;
import com.ongil.backend.domain.pricealert.repository.PriceAlertRepository;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PriceAlertService {

	private final PriceAlertRepository priceAlertRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	/**
	 * 할인 알림 설정 및 재설정
	 * 사용자가 상품 상세 화면에서 원하는 할인가를 선택하여 DB에 저장
	 * 실제 알림 발송은 PriceAlertScheduler가 주기적으로 가격을 확인하여 처리
	 */
	@Transactional
	public PriceAlert createOrUpdatePriceAlert(Long userId, PriceAlertRequest request) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

		Product product = productRepository.findById(request.getProductId())
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

		// 기존 활성 건이 있으면 비활성화 (재설정)
		priceAlertRepository.findByUserIdAndProductIdAndIsActiveTrue(userId, request.getProductId())
			.ifPresent(PriceAlert::deactivate);

		// 할인율로 목표 가격 계산 (할인가 있으면 할인가 기준, 없으면 원가 기준)
		int targetPrice = product.getEffectivePrice() * (100 - request.getDiscountRate()) / 100;

		// 새 건 생성
		PriceAlert priceAlert = PriceAlert.builder()
			.targetPrice(targetPrice)
			.isActive(true)
			.user(user)
			.product(product)
			.build();

		return priceAlertRepository.save(priceAlert);
	}

	// 현재 활성 중인 알림 조회
	@Transactional(readOnly = true)
	public PriceAlertResponse getPriceAlert(Long userId, Long productId) {

		PriceAlert priceAlert = priceAlertRepository
			.findByUserIdAndProductIdAndIsActiveTrue(userId, productId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.PRICE_ALERT_NOT_FOUND));

		return PriceAlertConverter.toResponse(priceAlert);
	}
}