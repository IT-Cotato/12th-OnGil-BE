package com.ongil.backend.domain.user.converter;

import java.text.NumberFormat;
import java.util.Locale;

import com.ongil.backend.domain.user.dto.response.UserInfoResDto;
import com.ongil.backend.domain.user.entity.User;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserConverter {

	public static UserInfoResDto toUserInfoResDto(User user) {
		return UserInfoResDto.builder()
			.userId(user.getId())
			.name(user.getName())
			.loginType(user.getLoginType())
			.phone(user.getPhone())
			.profileUrl(user.getProfileImg())
			.height(user.getHeight())
			.weight(user.getWeight())
			.usualTopSize(user.getUsualTopSize())
			.usualBottomSize(user.getUsualBottomSize())
			.usualShoeSize(user.getUsualShoeSize())
			.points(formatPoints(user.getPoints()))
			.build();
	}

	private static String formatPoints(Integer points) {
		if (points == null) {
			return "0";
		}
		return NumberFormat.getNumberInstance(Locale.KOREA).format(points);
	}
}
