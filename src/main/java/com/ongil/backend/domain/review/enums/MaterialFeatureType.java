package com.ongil.backend.domain.review.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum MaterialFeatureType {
    TEXTURE("촉감", Arrays.asList("부드러움", "거칠음")),
    WEIGHT("무게감", Arrays.asList("가벼움", "무거움")),
    WRINKLE("구김 정도", Arrays.asList("없음", "많음")),
    THICKNESS("두께감", Arrays.asList("두꺼움", "얇음")),
    PILLING("보풀", Arrays.asList("없음", "있음")),
    TRANSPARENCY("비침 정도", Arrays.asList("안비쳐요", "비쳐요"));

    private final String displayName;
    private final List<String> values;
}
