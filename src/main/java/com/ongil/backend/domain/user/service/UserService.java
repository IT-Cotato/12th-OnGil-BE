package com.ongil.backend.domain.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ongil.backend.domain.user.converter.BodyInfoConverter;
import com.ongil.backend.domain.user.converter.UserConverter;
import com.ongil.backend.domain.user.dto.request.BodyInfoRequest;
import com.ongil.backend.domain.user.dto.response.BodyInfoResponse;
import com.ongil.backend.domain.user.dto.response.SizeOptionsResponse;
import com.ongil.backend.domain.user.dto.response.TermsResponse;
import com.ongil.backend.domain.user.dto.response.UserInfoResDto;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.enums.BottomSize;
import com.ongil.backend.domain.user.enums.ShoeSize;
import com.ongil.backend.domain.user.enums.TopSize;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.config.s3.S3ImageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	private final UserRepository userRepository;
	private final S3ImageService s3ImageService;

	// 1. 내 정보 조회
	public UserInfoResDto getUserInfo(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));

		return UserConverter.toUserInfoResDto(user);
	}

	// 2. 프로필 이미지 변경
	@Transactional
	public UserInfoResDto updateProfileImage(Long userId, MultipartFile imageFile) {
		User user = findUser(userId);

		// 기존 프로필 이미지가 있으면 S3에서 삭제
		if (user.getProfileImg() != null) {
			s3ImageService.delete(user.getProfileImg());
		}

		// 새 이미지 S3 업로드
		String newImageUrl = s3ImageService.upload(imageFile);

		// DB 업데이트
		user.updateProfileImage(newImageUrl);

		return UserConverter.toUserInfoResDto(user);
	}

	// 2-1. 프로필 이미지 삭제 (기본 이미지로 초기화)
	@Transactional
	public UserInfoResDto deleteProfileImage(Long userId) {
		User user = findUser(userId);

		if (user.getProfileImg() != null) {
			s3ImageService.delete(user.getProfileImg());
			user.updateProfileImage(null);
		}

		return UserConverter.toUserInfoResDto(user);
	}

	// 공통 유저 찾기 메서드
	private User findUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException(ErrorCode.USER_NOT_FOUND));
	}

	// 3. 체형 정보 조회
	public BodyInfoResponse getBodyInfo(Long userId) {
		User user = findUser(userId);
		return BodyInfoConverter.toResponse(user);
	}

	// 4. 체형 정보 저장/수정
	@Transactional
	public BodyInfoResponse updateBodyInfo(Long userId, BodyInfoRequest request) {
		User user = findUser(userId);

		// Enum 유효성 검증 (잘못된 값이면 예외 발생)
		TopSize.fromDisplayName(request.topSize());
		BottomSize.fromDisplayName(request.bottomSize());
		ShoeSize.fromDisplayName(request.shoeSize());

		// 체형 정보 업데이트
		user.updateBodyInfo(
			request.height(),
			request.weight(),
			request.topSize(),
			request.bottomSize(),
			request.shoeSize(),
			request.agreedToCollection()
		);

		return BodyInfoConverter.toResponse(user);
	}

	// 5. 사이즈 옵션 목록 조회
	public SizeOptionsResponse getSizeOptions() {
		return BodyInfoConverter.toSizeOptionsResponse();
	}

	// 6. 수집·이용 동의 약관 조회
	public TermsResponse getBodyInfoTerms() {
		return TermsResponse.builder()
			.title("사이즈 정보 수집·이용 동의")
			.content("1. 수집 항목\n" +
				"- 키, 몸무게, 상의 사이즈, 하의 사이즈, 신발 사이즈\n\n" +
				"2. 수집 목적\n" +
				"- 맞춤형 사이즈 추천 서비스 제공\n" +
				"- 리뷰 작성 시 체형 정보 자동 입력\n\n" +
				"3. 보유 기간\n" +
				"- 회원 탈퇴 시까지\n\n" +
				"4. 동의 거부권 및 불이익\n" +
				"- 동의를 거부할 권리가 있으며, 거부 시 맞춤 사이즈 추천 서비스 이용이 제한됩니다.")
			.version("1.0")
			.effectiveDate("2026-01-01")
			.build();
	}
}
