package com.ongil.backend.domain.navigation.dto.response;

import com.ongil.backend.domain.navigation.enums.NavigationType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NavigationResponse {

	@Schema(description = "네비게이션 ID", example = "1")
	private Long navigationId;

	@Schema(description = "네비게이션 타입", example = "HOME")
	private NavigationType type;

	@Schema(description = "표시 라벨", example = "홈")
	private String label;

	@Schema(description = "라우트 경로", example = "/home")
	private String route;

	@Schema(description = "아이콘 URL", example = "https://...")
	private String iconUrl;

	@Schema(description = "표시 순서", example = "1")
	private Integer displayOrder;

	@Schema(description = "활성화 여부", example = "true")
	private Boolean enabled;
}
