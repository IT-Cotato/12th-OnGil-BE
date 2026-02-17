package com.ongil.backend.global.config.s3;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ImageService {

	private final S3Client s3Client;

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${spring.cloud.aws.region.static}")
	private String region;

	private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png");
	private static final String PROFILE_DIRECTORY = "profile";
	private static final String REVIEW_DIRECTORY = "review";

	public String uploadProfileImage(MultipartFile file) {
		return upload(file, PROFILE_DIRECTORY);
	}

	public String uploadReviewImage(MultipartFile file) {
		return upload(file, REVIEW_DIRECTORY);
	}

	/**
	 * 이미지를 S3에 업로드하고 공개 URL을 반환한다.
	 */
	private String upload(MultipartFile file, String directory) {
		validateFile(file);

		String extension = extractExtension(file.getOriginalFilename());
		String key = directory + "/" + UUID.randomUUID() + "." + extension;

		try {
			PutObjectRequest putRequest = PutObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.contentType(file.getContentType())
				.build();

			s3Client.putObject(putRequest,
				RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
		} catch (IOException | SdkException e) {
			log.error("S3 업로드 실패: {}", e.getMessage());
			throw new AppException(ErrorCode.S3_UPLOAD_FAILED);
		}

		return generateUrl(key);
	}

	/**
	 * S3에서 기존 이미지를 삭제한다.
	 */
	public void delete(String imageUrl) {
		if (!imageUrl.startsWith(generatePrefix())) {
			log.info("S3 URL이 아니므로 삭제 생략: {}", imageUrl);
			return;
		}

		String key = extractKey(imageUrl);

		try {
			DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build();

			s3Client.deleteObject(deleteRequest);
			log.info("S3 이미지 삭제 완료: {}", key);
		} catch (SdkException e) {
			log.error("S3 이미지 삭제 실패: {}", e.getMessage());
			throw new AppException(ErrorCode.S3_DELETE_FAILED);
		}
	}

	private void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new AppException(ErrorCode.FILE_IS_EMPTY);
		}

		String extension = extractExtension(file.getOriginalFilename());
		if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
			throw new AppException(ErrorCode.INVALID_FILE_EXTENSION);
		}
	}

	private String extractExtension(String originalFilename) {
		if (originalFilename == null || !originalFilename.contains(".")) {
			throw new AppException(ErrorCode.INVALID_FILE_EXTENSION);
		}
		return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
	}

	/**
	 * S3 공개 URL에서 key(경로) 부분만 추출한다.
	 * 예: "https://ongil-bucket.s3.ap-northeast-2.amazonaws.com/profile/abc.jpg"
	 *   -> "profile/abc.jpg"
	 */
	private String extractKey(String imageUrl) {
		String prefix = generatePrefix();
		if (!imageUrl.startsWith(prefix)) {
			log.error("예상 외 이미지 URL 형식: {}", imageUrl);
			throw new AppException(ErrorCode.S3_DELETE_FAILED);
		}
		return imageUrl.substring(prefix.length());
	}

	private String generatePrefix() {
		return "https://" + bucket + ".s3." + region + ".amazonaws.com/";
	}

	private String generateUrl(String key) {
		return generatePrefix() + key;
	}
}