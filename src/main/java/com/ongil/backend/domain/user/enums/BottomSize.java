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
public enum BottomSize {
	SIZE_22_OR_LESS("22이하"),
	SIZE_23("23"),
	SIZE_24("24"),
	SIZE_25("25"),
	SIZE_26("26"),
	SIZE_27("27"),
	SIZE_28("28"),
	SIZE_29("29"),
	SIZE_30("30"),
	SIZE_31("31"),
	SIZE_32("32"),
	SIZE_33("33"),
	SIZE_34("34"),
	SIZE_35("35"),
	SIZE_36("36"),
	SIZE_37("37"),
	SIZE_38("38"),
	SIZE_39("39"),
	SIZE_40_OR_MORE("40이상");

	private final String displayName;

	public static BottomSize fromDisplayName(String displayName) {
		return Arrays.stream(values())
			.filter(size -> size.displayName.equals(displayName))
			.findFirst()
			.orElseThrow(() -> new AppException(ErrorCode.INVALID_SIZE));
	}

	public static List<String> getAllDisplayNames() {
		return Arrays.stream(values())
			.map(BottomSize::getDisplayName)
			.collect(Collectors.toList());
	}
}