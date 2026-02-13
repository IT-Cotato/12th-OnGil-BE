package com.ongil.backend.domain.review.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SizeAnswer {
    TIGHT_IMMEDIATELY("입자마자 답답", true),
    TIGHT_WHEN_MOVING("움직이면 답답", true),
    COMFORTABLE("편함", false),
    LOOSE("헐렁함", true),
    TOO_BIG_NEED_ALTERATION("너무 큼, 수선필요", true);

    private final String displayName;
    private final boolean needsSecondaryQuestion;
}
