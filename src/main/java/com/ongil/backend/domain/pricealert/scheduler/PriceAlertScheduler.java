package com.ongil.backend.domain.pricealert.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.notification.converter.NotificationConverter;
import com.ongil.backend.domain.notification.dto.response.NotificationResponse;
import com.ongil.backend.domain.notification.entity.Notification;
import com.ongil.backend.domain.notification.repository.NotificationRepository;
import com.ongil.backend.domain.notification.service.NotificationSseService;
import com.ongil.backend.domain.pricealert.entity.PriceAlert;
import com.ongil.backend.domain.pricealert.repository.PriceAlertRepository;
import com.ongil.backend.domain.product.entity.Product;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceAlertScheduler {

	private final PriceAlertRepository priceAlertRepository;
	private final NotificationRepository notificationRepository;
	private final NotificationSseService notificationSseService;

	@Scheduled(fixedRate = 30000) // 30초 간격
	@Transactional
	public void checkPriceAlerts() {

		// 활성 중이고 알림 미발송인 건만 조회
		List<PriceAlert> alerts = priceAlertRepository.findActiveAlertsWithUserAndProduct();

		for (PriceAlert alert : alerts) {
			Product product = alert.getProduct();
			Integer currentPrice = product.getEffectivePrice();
			Integer targetPrice = alert.getTargetPrice();

			if (currentPrice <= targetPrice) {

				// 1. PriceAlert의 isNotified를 true로 업데이트
				alert.markAsNotified();

				Notification notification = Notification.builder()
					.message(product.getName() + "이(가) " + String.format("%,d", currentPrice) + "원으로 할인되었습니다!")
					.targetUrl("/products/" + product.getId())
					.notifiedAt(LocalDateTime.now())
					.user(alert.getUser())
					.product(product)
					.build();

				Notification savedNotification = notificationRepository.save(notification);

				// 3. SSE로 실시간 알림 전송
				Long userId = alert.getUser().getId();
				NotificationResponse response = NotificationConverter.toResponse(savedNotification);
				notificationSseService.sendNotification(userId, response);

				log.info("할인 알림 발송 - userId: {}, productId: {}, currentPrice: {}, targetPrice: {}",
					userId, product.getId(), currentPrice, targetPrice);
			}
		}
	}
}