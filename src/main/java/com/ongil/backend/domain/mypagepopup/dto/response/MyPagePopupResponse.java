package com.ongil.backend.domain.mypagepopup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MyPagePopupResponse(
        @Schema(description = "팝업 ID")
        Long id,

        @Schema(description = "팝업 제목")
        String title,

        @Schema(description = "팝업 설명")
        String description,

        @Schema(description = "이미지 URL")
        String imageUrl,

        @Schema(description = "액션 URL (클릭 시 이동할 링크)")
        String actionUrl,

        @Schema(description = "우선순위 (높을수록 먼저 표시)")
        Integer priority
) {
}
