package com.ongil.backend.domain.magazine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentReqDto(
	@NotBlank(message = "댓글 내용은 비어있을 수 없습니다.")
	@Size(max = 500, message = "댓글은 최대 500자까지 입력 가능합니다.")
	String content
) {}
