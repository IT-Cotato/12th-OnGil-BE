package com.ongil.backend.domain.magazine.converter;

import com.ongil.backend.domain.magazine.dto.response.CommentResDto;
import com.ongil.backend.domain.magazine.entity.MagazineComment;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CommentConverter {
	public static CommentResDto from(MagazineComment comment) {
		return CommentResDto.builder()
			.commentId(comment.getId())
			.userId(comment.getUser().getId())
			.userName(comment.getUser().getName())
			.userProfileImage(comment.getUser().getProfileImg())
			.content(comment.getContent())
			.likeCount(comment.getLikeCount())
			.createdAt(comment.getCreatedAt().toString())
			.build();
	}
}