package com.ongil.backend.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoginType {
	GOOGLE("GOOGLE"),
	KAKAO("KAKAO"),
	GENERAL("GENERAL");

	private final String value;
}
