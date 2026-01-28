package com.ongil.backend.domain.mypage.dto.response;

import com.ongil.backend.domain.mypage.enums.MyPageMenuType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MyPageMenuItemResponse(
	@Schema(description = "메뉴 타입", example = "PROFILE")
	MyPageMenuType type,

	@Schema(description = "메뉴 표시명", example = "내 정보")
	String displayName,

	@Schema(description = "메뉴 API 경로", example = "/api/users/me")
	String apiPath,

	@Schema(description = "메뉴 설명", example = "프로필 및 개인정보 관리")
	String description,

	@Schema(description = "배지 카운트 (선택 사항)", example = "3")
	Integer badgeCount
) {
}
