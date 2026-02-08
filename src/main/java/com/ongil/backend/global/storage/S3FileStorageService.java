package com.ongil.backend.global.storage;

import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3FileStorageService {

	private final S3Client s3Client;
	private final S3Properties s3Properties;

	private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
	private static final String[] ALLOWED_CONTENT_TYPES = {
		"image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
	};

	/**
	 * 프로필 이미지 파일을 S3에 업로드
	 *
	 * @param file 업로드할 파일
	 * @param userId 사용자 ID
	 * @return S3에 저장된 파일의 URL
	 */
	public String uploadProfileImage(MultipartFile file, Long userId) {
		validateFile(file);

		String fileName = generateFileName(file, userId);
		String key = "profile-images/" + fileName;

		try {
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(s3Properties.getBucketName())
				.key(key)
				.contentType(file.getContentType())
				.contentLength(file.getSize())
				.build();

			s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

			return getFileUrl(key);
		} catch (IOException e) {
			log.error("Failed to upload file to S3", e);
			throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
		}
	}

	/**
	 * S3에서 파일 삭제
	 *
	 * @param fileUrl 삭제할 파일의 URL
	 */
	public void deleteFile(String fileUrl) {
		if (fileUrl == null || fileUrl.isEmpty()) {
			return;
		}

		try {
			String key = extractKeyFromUrl(fileUrl);
			DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
				.bucket(s3Properties.getBucketName())
				.key(key)
				.build();

			s3Client.deleteObject(deleteObjectRequest);
			log.info("Successfully deleted file from S3: {}", key);
		} catch (Exception e) {
			log.error("Failed to delete file from S3: {}", fileUrl, e);
			// 삭제 실패는 치명적이지 않으므로 예외를 던지지 않음
		}
	}

	private void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new AppException(ErrorCode.FILE_NOT_PROVIDED);
		}

		// 파일 크기 검증
		if (file.getSize() > MAX_FILE_SIZE) {
			throw new AppException(ErrorCode.FILE_SIZE_EXCEEDED);
		}

		// 파일 타입 검증
		String contentType = file.getContentType();
		boolean isValidType = false;
		for (String allowedType : ALLOWED_CONTENT_TYPES) {
			if (allowedType.equals(contentType)) {
				isValidType = true;
				break;
			}
		}

		if (!isValidType) {
			throw new AppException(ErrorCode.INVALID_FILE_TYPE);
		}
	}

	private String generateFileName(MultipartFile file, Long userId) {
		String originalFilename = file.getOriginalFilename();
		String extension = "";
		if (originalFilename != null && originalFilename.contains(".")) {
			extension = originalFilename.substring(originalFilename.lastIndexOf("."));
		}
		return userId + "_" + UUID.randomUUID() + extension;
	}

	private String getFileUrl(String key) {
		return String.format("https://%s.s3.%s.amazonaws.com/%s",
			s3Properties.getBucketName(),
			s3Properties.getRegion(),
			key);
	}

	private String extractKeyFromUrl(String fileUrl) {
		// URL에서 키 추출: https://bucket.s3.region.amazonaws.com/key
		String baseUrl = String.format("https://%s.s3.%s.amazonaws.com/",
			s3Properties.getBucketName(),
			s3Properties.getRegion());
		return fileUrl.replace(baseUrl, "");
	}
}
