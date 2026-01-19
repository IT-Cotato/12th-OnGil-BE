package com.ongil.backend.domain.product.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "사이즈 가이드 응답")
public class SizeGuideResponse {

	@Schema(description = "추천 사이즈 목록 (동률인 경우 여러 개)", example = "[\"M\", \"L\"]")
	private List<String> recommendedSizes;

	@Schema(description = "사이즈별 구매 통계 (바 그래프용)")
	private List<SizeStatistic> sizeStatistics;

	@Schema(description = "유사 고객 구매 정보 (표 형식용, 최대 4개)")
	private List<SimilarCustomer> similarCustomers;

	@Schema(description = "사용자 체형 정보")
	private UserBodyInfo userBodyInfo;

	@Getter
	@Builder
	@Schema(description = "사이즈별 구매 통계")
	public static class SizeStatistic {

		@Schema(description = "사이즈", example = "M")
		private String size;

		@Schema(description = "구매 횟수", example = "15")
		private Long purchaseCount;
	}

	@Getter
	@Builder
	@Schema(description = "유사 고객 구매 정보")
	public static class SimilarCustomer {

		@Schema(description = "키 (cm)", example = "165")
		private Integer height;

		@Schema(description = "몸무게 (kg)", example = "60")
		private Integer weight;

		@Schema(description = "구매한 사이즈", example = "M")
		private String purchasedSize;
	}

	@Getter
	@Builder
	@Schema(description = "사용자 체형 정보")
	public static class UserBodyInfo {

		@Schema(description = "키 (cm)", example = "165")
		private Integer height;

		@Schema(description = "몸무게 (kg)", example = "60")
		private Integer weight;

		@Schema(description = "평소 사이즈 (카테고리별로 다름)", example = "M")
		private String usualSize;
	}
}