package com.ongil.backend.domain.product.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiMaterialDescriptionResponse {

	private String advantages;
	private String disadvantages;
	private String care;

	public static AiMaterialDescriptionResponse createDefault() {
		return AiMaterialDescriptionResponse.builder()
			.advantages("착용감이 좋습니다\n품질이 우수합니다")
			.disadvantages("특별한 단점이 없습니다")
			.care("제품 라벨의 세탁 방법을 따라주세요")
			.build();
	}
}
