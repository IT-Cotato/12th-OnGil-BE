package com.ongil.backend.domain.magazine.parser;

import java.util.Optional;

import com.ongil.backend.domain.magazine.entity.Magazine;
import com.ongil.backend.domain.magazine.enums.MagazineCategory;

public interface MagazineArticleParser {
	Optional<Magazine> parse(String url, MagazineCategory category);
}
