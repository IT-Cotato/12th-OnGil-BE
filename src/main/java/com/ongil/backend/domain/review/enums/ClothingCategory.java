package com.ongil.backend.domain.review.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ClothingCategory {
    OUTER("아우터", Arrays.asList(
        "전반적", "어깨&목", "가슴&몸통", "겨드랑이&팔"
    )),
    TOP("상의", Arrays.asList(
        "전반적", "어깨&목", "가슴&몸통", "겨드랑이&팔"
    )),
    SKIRT("스커트", Arrays.asList(
        "전반적", "허리&복부", "엉덩이&가랑이", "허벅지&종아리"
    )),
    DRESS("원피스", Arrays.asList(
        "전반적", "목&어깨", "가슴&몸통", "겨드랑이&팔", "엉덩이&다리", "기장"
    )),
    PANTS("팬츠", Arrays.asList(
        "전반적", "허리&복부", "엉덩이&가랑이", "허벅지&종아리"
    ));

    private final String displayName;
    private final List<String> bodyParts;

    public static ClothingCategory fromDisplayName(String name) {
        return Arrays.stream(ClothingCategory.values())
            .filter(category -> category.getDisplayName().equals(name))
            .findFirst()
            .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

}
