package com.ongil.backend.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
	USER("일반 사용자"),
	ADMIN("관리자");

	private final String description;
}
