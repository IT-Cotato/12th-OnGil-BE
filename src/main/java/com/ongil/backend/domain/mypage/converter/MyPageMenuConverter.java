package com.ongil.backend.domain.mypage.converter;

import java.util.List;

import com.ongil.backend.domain.mypage.dto.response.MyPageMenuItemResponse;
import com.ongil.backend.domain.mypage.dto.response.MyPageMenuResponse;
import com.ongil.backend.domain.mypage.enums.MyPageMenuType;

public class MyPageMenuConverter {

	public static MyPageMenuItemResponse toMenuItemResponse(MyPageMenuType type, Integer badgeCount) {
		return MyPageMenuItemResponse.builder()
			.type(type)
			.displayName(type.getDisplayName())
			.apiPath(type.getApiPath())
			.description(type.getDescription())
			.badgeCount(badgeCount)
			.build();
	}

	public static MyPageMenuResponse toMenuResponse(List<MyPageMenuItemResponse> menuItems) {
		return MyPageMenuResponse.builder()
			.menuItems(menuItems)
			.build();
	}
}
