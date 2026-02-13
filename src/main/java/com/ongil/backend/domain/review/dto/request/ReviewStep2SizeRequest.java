package com.ongil.backend.domain.review.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewStep2SizeRequest {

    @NotEmpty(message = "불편했던 부위를 최소 1개 이상 선택해주세요.")
    private List<String> fitIssueParts;

}
