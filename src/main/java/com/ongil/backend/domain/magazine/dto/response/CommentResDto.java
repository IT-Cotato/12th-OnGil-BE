package com.ongil.backend.domain.magazine.dto.response;

import lombok.Builder;

@Builder
public record CommentResDto(
	Long commentId,
	Long userId,
	String userName,
	String userProfileImage,
	String content,
	Integer likeCount,
	String createdAt
) {
}
