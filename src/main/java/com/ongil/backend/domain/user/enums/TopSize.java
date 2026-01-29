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
public enum TopSize {
	SIZE_44_OR_LESS("44이하"),
	SIZE_55("55"),
	SIZE_66("66"),
	SIZE_77("77"),
	SIZE_88("88"),
	SIZE_99_OR_MORE("99이상");

	private final String displayName;

	public static TopSize fromDisplayName(String displayName) {
		return Arrays.stream(values())
			.filter(size -> size.displayName.equals(displayName))
			.findFirst()
			.orElseThrow(() -> new AppException(ErrorCode.INVALID_SIZE));
	}

	public static List<String> getAllDisplayNames() {
		return Arrays.stream(values())
			.map(TopSize::getDisplayName)
			.collect(Collectors.toList());
	}
}