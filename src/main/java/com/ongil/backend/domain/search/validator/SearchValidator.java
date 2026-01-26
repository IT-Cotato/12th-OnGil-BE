package com.ongil.backend.domain.search.validator;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SearchValidator {

	public static String normalize(String q) {
		if (q == null) return "";
		return q.trim().replaceAll("\\s+", " ");
	}

	public static boolean isNoiseKeyword(String keyword) {
		return keyword.isEmpty() || keyword.matches("^[ㄱ-ㅎㅏ-ㅣ]$");
	}
}
