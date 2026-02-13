package com.ongil.backend.domain.review.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ColorAnswer {
    BRIGHTER_THAN_SCREEN("화면보다 밝음"),
    SAME_AS_SCREEN("화면과 똑같음"),
    DARKER_THAN_SCREEN("어두움");

    private final String displayName;
}
