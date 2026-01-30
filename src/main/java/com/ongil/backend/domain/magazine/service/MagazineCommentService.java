package com.ongil.backend.domain.magazine.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import com.ongil.backend.domain.magazine.converter.CommentConverter;
import com.ongil.backend.domain.magazine.dto.response.CommentResDto;
import com.ongil.backend.domain.magazine.entity.Magazine;
import com.ongil.backend.domain.magazine.entity.MagazineComment;
import com.ongil.backend.domain.magazine.entity.MagazineCommentLike;
import com.ongil.backend.domain.magazine.repository.CommentLikeRepository;
import com.ongil.backend.domain.magazine.repository.MagazineCommentRepository;
import com.ongil.backend.domain.magazine.repository.MagazineRepository;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MagazineCommentService {

	private final MagazineCommentRepository commentRepository;
	private final MagazineRepository magazineRepository;
	private final UserRepository userRepository;
	private final CommentLikeRepository commentLikeRepository;

	@Transactional
	public CommentResDto createComment(Long magazineId, Long userId, String content) {
		Magazine magazine = magazineRepository.findById(magazineId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.MAGAZINE_NOT_FOUND));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

		MagazineComment comment = MagazineComment.builder()
			.content(content)
			.user(user)
			.magazine(magazine)
			.likeCount(0)
			.build();

		MagazineComment savedComment = commentRepository.save(comment);

		return CommentConverter.from(savedComment);
	}

	public List<CommentResDto> getComments(Long magazineId) {
		return commentRepository.findByMagazineIdOrderByCreatedAtDesc(magazineId)
			.stream()
			.map(CommentConverter::from)
			.toList();
	}

	@Transactional
	public boolean toggleCommentLike(Long commentId, Long userId) {
		MagazineComment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.COMMENT_NOT_FOUND));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

		Optional<MagazineCommentLike> existingLike = commentLikeRepository.findByCommentAndUser(comment, user);

		if (existingLike.isPresent()) {
			commentLikeRepository.delete(existingLike.get());
			comment.decreaseLikeCount();
			return false;
		} else {
			MagazineCommentLike newLike = MagazineCommentLike.builder()
				.comment(comment)
				.user(user)
				.build();
			commentLikeRepository.save(newLike);
			comment.increaseLikeCount();
			return true;
		}
	}

}