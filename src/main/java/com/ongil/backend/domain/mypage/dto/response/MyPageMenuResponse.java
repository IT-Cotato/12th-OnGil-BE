package com.ongil.backend.domain.mypage.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MyPageMenuResponse(
	@Schema(description = "마이페이지 메뉴 아이템 리스트")
	List<MyPageMenuItemResponse> menuItems
) {
}
