package com.ongil.backend.domain.auth.dto.response;

public record GoogleUserInfoResDto(
	String sub,      // 구글의 고유 식별자 (카카오의 id 역할)
	String name,
	String email,
	String picture
) {}
