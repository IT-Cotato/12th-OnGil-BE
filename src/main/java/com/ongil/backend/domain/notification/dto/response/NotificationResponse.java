package com.ongil.backend.domain.notification.dto.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "알림 응답")
public class NotificationResponse {

	@Schema(description = "알림 ID", example = "1")
	private Long notificationId;

	@Schema(description = "알림 메시지", example = "오버핏 니트이(가) 47,500원으로 할인되었습니다!")
	private String message;

	@Schema(description = "클릭 시 이동할 URL", example = "/products/1")
	private String targetUrl;

	@Schema(description = "읽음 여부", example = "false")
	private boolean read;

	@Schema(description = "알림 발송 시각")
	private LocalDateTime notifiedAt;
}