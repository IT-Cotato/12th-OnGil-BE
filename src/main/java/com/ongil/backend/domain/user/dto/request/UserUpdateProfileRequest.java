package com.ongil.backend.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserUpdateProfileRequest(
        @Schema(description = "변경할 프로필 이미지 URL", example = "https://s3.aws.com/new-profile.jpg")
        String profileImageUrl
) {
}