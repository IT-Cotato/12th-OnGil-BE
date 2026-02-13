package com.ongil.backend.domain.review.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaterialAnswer {
    VERY_GOOD("너무 좋음", true),
    GOOD("좋음", true),
    NORMAL("무난함", false),
    BAD("아쉬움", true),
    VERY_BAD("너무 아쉬움", true);

    private final String displayName;
    private final boolean needsSecondaryQuestion;

    public boolean isPositive() {
        return this == VERY_GOOD || this == GOOD;
    }
}
