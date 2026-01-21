package com.ongil.backend.domain.search.util;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class SynonymProvider {

    private static final Map<String, String> SYNONYM_MAP = new HashMap<>();

    static {
        SYNONYM_MAP.put("랩탑", "노트북");
        SYNONYM_MAP.put("폰", "스마트폰");
    }

    public String getRefinedKeyword(String keyword) {
        return SYNONYM_MAP.getOrDefault(keyword, keyword);
    }
}