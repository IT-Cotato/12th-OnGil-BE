package com.ongil.backend.domain.auth.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginReqDto(
	@NotNull(message = "아이디를 입력해주세요.")
	@Size(min = 4, message = "아이디는 4자 이상이어야 합니다.")
	String loginId,

	@NotNull(message = "비밀번호를 입력해주세요.")
	@Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다.")
	String password
) {
}
