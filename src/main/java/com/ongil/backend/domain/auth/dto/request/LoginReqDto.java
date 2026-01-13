package com.ongil.backend.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginReqDto(
	@NotBlank(message = "아이디를 입력해주세요.")
	@Size(min = 4, message = "아이디는 4자 이상이어야 합니다.")
	String loginId,

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다.")
	String password
) {
}
