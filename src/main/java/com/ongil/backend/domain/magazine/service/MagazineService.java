package com.ongil.backend.domain.magazine.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import com.ongil.backend.domain.magazine.converter.MagazineConverter;
import com.ongil.backend.domain.magazine.dto.response.MagazineResDto;
import com.ongil.backend.domain.magazine.entity.Magazine;
import com.ongil.backend.domain.magazine.entity.MagazineBookmark;
import com.ongil.backend.domain.magazine.enums.MagazineCategory;
import com.ongil.backend.domain.magazine.repository.MagazineBookmarkRepository;
import com.ongil.backend.domain.magazine.repository.MagazineRepository;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.global.common.exception.AppException;
import com.ongil.backend.global.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MagazineService {

	private final MagazineRepository magazineRepository;
	private final MagazineBookmarkRepository bookmarkRepository;
	private final UserRepository userRepository;

	public List<MagazineResDto> getRecommendedMagazines() {
		return magazineRepository.findRandomRecommended(PageRequest.of(0, 6))
			.stream()
			.map(MagazineConverter::from)
			.toList();
	}


	public List<MagazineResDto> getMagazinesByCategory(MagazineCategory category) {
		MagazineCategory targetCategory = (category != null) ? category : MagazineCategory.PRICE;

		return magazineRepository.findByCategoryOrderByViewCountDescPublishedAtDesc(targetCategory, PageRequest.of(0, 15))
			.stream()
			.map(MagazineConverter::from)
			.toList();
	}

	@Transactional
	public MagazineResDto getMagazineDetail(Long magazineId) {
		Magazine magazine = magazineRepository.findById(magazineId)
			.orElseThrow(() -> new AppException(ErrorCode.MAGAZINE_NOT_FOUND));

		magazine.addViewCount();

		return MagazineConverter.from(magazine);
	}

	@Transactional
	public boolean toggleBookmark(Long magazineId, Long userId) {
		Magazine magazine = magazineRepository.findById(magazineId)
			.orElseThrow(() -> new AppException(ErrorCode.MAGAZINE_NOT_FOUND));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

		Optional<MagazineBookmark> bookmark = bookmarkRepository.findByUserAndMagazine(user, magazine);

		if (bookmark.isPresent()) {
			bookmarkRepository.delete(bookmark.get());
			return false;
		} else {
			bookmarkRepository.save(MagazineBookmark.builder()
				.user(user)
				.magazine(magazine)
				.build());
			return true;
		}
	}

	@Transactional(readOnly = true)
	public List<MagazineResDto> getBookmarkedMagazines(Long userId, MagazineCategory category) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

		MagazineCategory targetCategory = (category != null) ? category : MagazineCategory.PRICE;

		List<MagazineBookmark> bookmarks =
			bookmarkRepository.findByUserAndMagazineCategoryOrderByCreatedAtDesc(user, targetCategory);

		return bookmarks.stream()
			.map(bookmark -> MagazineConverter.from(bookmark.getMagazine()))
			.toList();
	}
}