package com.ongil.backend.domain.user.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ShoeSize {
	SIZE_210_OR_LESS("210이하"),
	SIZE_215("215"),
	SIZE_220("220"),
	SIZE_225("225"),
	SIZE_230("230"),
	SIZE_235("235"),
	SIZE_240("240"),
	SIZE_245("245"),
	SIZE_250("250"),
	SIZE_255("255"),
	SIZE_260("260"),
	SIZE_265("265"),
	SIZE_270("270"),
	SIZE_275("275"),
	SIZE_280_OR_MORE("280이상");

	private final String displayName;

	public static ShoeSize fromDisplayName(String displayName) {
		return Arrays.stream(values())
			.filter(size -> size.displayName.equals(displayName))
			.findFirst()
			.orElseThrow(() -> new AppException(ErrorCode.INVALID_SIZE));
	}

	public static List<String> getAllDisplayNames() {
		return Arrays.stream(values())
			.map(ShoeSize::getDisplayName)
			.collect(Collectors.toList());
	}
}