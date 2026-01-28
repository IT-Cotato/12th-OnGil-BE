package com.ongil.backend.domain.mypage.converter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ongil.backend.domain.mypage.dto.response.MyPageMenuItemResponse;
import com.ongil.backend.domain.mypage.dto.response.MyPageMenuResponse;
import com.ongil.backend.domain.mypage.enums.MyPageMenuType;

@Component
public class MyPageMenuConverter {

	public MyPageMenuItemResponse toMenuItemResponse(MyPageMenuType type, Integer badgeCount) {
		return MyPageMenuItemResponse.builder()
			.type(type)
			.displayName(type.getDisplayName())
			.apiPath(type.getApiPath())
			.description(type.getDescription())
			.badgeCount(badgeCount)
			.build();
	}

	public MyPageMenuResponse toMenuResponse(List<MyPageMenuItemResponse> menuItems) {
		return MyPageMenuResponse.builder()
			.menuItems(menuItems)
			.build();
	}
}
