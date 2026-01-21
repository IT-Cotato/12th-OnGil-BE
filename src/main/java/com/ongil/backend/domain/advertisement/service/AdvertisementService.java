package com.ongil.backend.domain.advertisement.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ongil.backend.domain.advertisement.converter.AdvertisementConverter;
import com.ongil.backend.domain.advertisement.dto.request.AdvertisementCreateRequest;
import com.ongil.backend.domain.advertisement.dto.request.AdvertisementUpdateRequest;
import com.ongil.backend.domain.advertisement.dto.request.UserAdPreferenceRequest;
import com.ongil.backend.domain.advertisement.dto.response.AdvertisementResponse;
import com.ongil.backend.domain.advertisement.dto.response.UserAdPreferenceResponse;
import com.ongil.backend.domain.advertisement.entity.Advertisement;
import com.ongil.backend.domain.advertisement.entity.UserAdPreference;
import com.ongil.backend.domain.advertisement.enums.AdvertisementType;
import com.ongil.backend.domain.advertisement.repository.AdvertisementRepository;
import com.ongil.backend.domain.advertisement.repository.UserAdPreferenceRepository;
import com.ongil.backend.domain.brand.entity.Brand;
import com.ongil.backend.domain.brand.repository.BrandRepository;
import com.ongil.backend.domain.category.entity.Category;
import com.ongil.backend.domain.category.repository.CategoryRepository;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdvertisementService {

	private final AdvertisementRepository advertisementRepository;
	private final UserAdPreferenceRepository userAdPreferenceRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final BrandRepository brandRepository;

	@Transactional
	public AdvertisementResponse createAdvertisement(AdvertisementCreateRequest request) {
		Category targetCategory = null;
		if (request.getTargetCategoryId() != null) {
			targetCategory = categoryRepository.findById(request.getTargetCategoryId())
				.orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다"));
		}

		Brand targetBrand = null;
		if (request.getTargetBrandId() != null) {
			targetBrand = brandRepository.findById(request.getTargetBrandId())
				.orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없습니다"));
		}

		Advertisement advertisement = Advertisement.builder()
			.title(request.getTitle())
			.description(request.getDescription())
			.imageUrl(request.getImageUrl())
			.targetUrl(request.getTargetUrl())
			.advertisementType(request.getAdvertisementType())
			.displayOrder(request.getDisplayOrder())
			.targetCategory(targetCategory)
			.targetBrand(targetBrand)
			.startDate(request.getStartDate())
			.endDate(request.getEndDate())
			.isActive(request.getIsActive())
			.build();

		Advertisement saved = advertisementRepository.save(advertisement);
		return AdvertisementConverter.toResponse(saved);
	}

	@Transactional
	public AdvertisementResponse updateAdvertisement(Long advertisementId, AdvertisementUpdateRequest request) {
		Advertisement advertisement = advertisementRepository.findById(advertisementId)
			.orElseThrow(() -> new IllegalArgumentException("광고를 찾을 수 없습니다"));

		// Use reflection or builder pattern to update only non-null fields
		// For simplicity, updating directly
		Advertisement updated = Advertisement.builder()
			.title(request.getTitle() != null ? request.getTitle() : advertisement.getTitle())
			.description(request.getDescription() != null ? request.getDescription() : advertisement.getDescription())
			.imageUrl(request.getImageUrl() != null ? request.getImageUrl() : advertisement.getImageUrl())
			.targetUrl(request.getTargetUrl() != null ? request.getTargetUrl() : advertisement.getTargetUrl())
			.advertisementType(request.getAdvertisementType() != null ? request.getAdvertisementType() : advertisement.getAdvertisementType())
			.displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : advertisement.getDisplayOrder())
			.targetCategory(request.getTargetCategoryId() != null ? 
				categoryRepository.findById(request.getTargetCategoryId()).orElse(null) : advertisement.getTargetCategory())
			.targetBrand(request.getTargetBrandId() != null ? 
				brandRepository.findById(request.getTargetBrandId()).orElse(null) : advertisement.getTargetBrand())
			.startDate(request.getStartDate() != null ? request.getStartDate() : advertisement.getStartDate())
			.endDate(request.getEndDate() != null ? request.getEndDate() : advertisement.getEndDate())
			.isActive(request.getIsActive() != null ? request.getIsActive() : advertisement.getIsActive())
			.build();

		Advertisement saved = advertisementRepository.save(updated);
		return AdvertisementConverter.toResponse(saved);
	}

	@Transactional
	public void deleteAdvertisement(Long advertisementId) {
		Advertisement advertisement = advertisementRepository.findById(advertisementId)
			.orElseThrow(() -> new IllegalArgumentException("광고를 찾을 수 없습니다"));
		advertisementRepository.delete(advertisement);
	}

	public AdvertisementResponse getAdvertisement(Long advertisementId) {
		Advertisement advertisement = advertisementRepository.findById(advertisementId)
			.orElseThrow(() -> new IllegalArgumentException("광고를 찾을 수 없습니다"));
		return AdvertisementConverter.toResponse(advertisement);
	}

	public List<AdvertisementResponse> getAllAdvertisements() {
		return advertisementRepository.findAll().stream()
			.map(AdvertisementConverter::toResponse)
			.collect(Collectors.toList());
	}

	public List<AdvertisementResponse> getActiveAdvertisements() {
		LocalDateTime now = LocalDateTime.now();
		return advertisementRepository.findActiveAdvertisements(now).stream()
			.map(AdvertisementConverter::toResponse)
			.collect(Collectors.toList());
	}

	public List<AdvertisementResponse> getPersonalizedAdvertisements(Long userId) {
		LocalDateTime now = LocalDateTime.now();
		
		// Get user preferences
		List<UserAdPreference> preferences = userAdPreferenceRepository.findByUserIdAndIsInterestedTrue(userId);
		
		if (preferences.isEmpty()) {
			// Return default active advertisements if no preferences
			return getActiveAdvertisements();
		}

		// Get advertisements matching user preferences
		List<Advertisement> personalizedAds = new ArrayList<>();
		
		for (UserAdPreference pref : preferences) {
			if (pref.getPreferredCategory() != null) {
				personalizedAds.addAll(
					advertisementRepository.findActiveAdvertisementsByCategory(now, pref.getPreferredCategory().getId())
				);
			}
			if (pref.getPreferredBrand() != null) {
				personalizedAds.addAll(
					advertisementRepository.findActiveAdvertisementsByBrand(now, pref.getPreferredBrand().getId())
				);
			}
		}

		// Remove duplicates and sort by display order
		List<Advertisement> uniqueAds = personalizedAds.stream()
			.distinct()
			.sorted((a1, a2) -> a1.getDisplayOrder().compareTo(a2.getDisplayOrder()))
			.collect(Collectors.toList());

		// If no personalized ads found, return default active ads
		if (uniqueAds.isEmpty()) {
			return getActiveAdvertisements();
		}

		return uniqueAds.stream()
			.map(AdvertisementConverter::toResponse)
			.collect(Collectors.toList());
	}

	@Transactional
	public void incrementImpression(Long advertisementId) {
		Advertisement advertisement = advertisementRepository.findById(advertisementId)
			.orElseThrow(() -> new IllegalArgumentException("광고를 찾을 수 없습니다"));
		advertisement.incrementImpressionCount();
	}

	@Transactional
	public void incrementClick(Long advertisementId) {
		Advertisement advertisement = advertisementRepository.findById(advertisementId)
			.orElseThrow(() -> new IllegalArgumentException("광고를 찾을 수 없습니다"));
		advertisement.incrementClickCount();
	}

	// User Preference Methods
	@Transactional
	public UserAdPreferenceResponse createUserPreference(Long userId, UserAdPreferenceRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

		Category category = null;
		if (request.getPreferredCategoryId() != null) {
			category = categoryRepository.findById(request.getPreferredCategoryId())
				.orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다"));
		}

		Brand brand = null;
		if (request.getPreferredBrandId() != null) {
			brand = brandRepository.findById(request.getPreferredBrandId())
				.orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없습니다"));
		}

		UserAdPreference preference = UserAdPreference.builder()
			.user(user)
			.preferredCategory(category)
			.preferredBrand(brand)
			.isInterested(request.getIsInterested())
			.build();

		UserAdPreference saved = userAdPreferenceRepository.save(preference);
		return AdvertisementConverter.toPreferenceResponse(saved);
	}

	@Transactional
	public UserAdPreferenceResponse updateUserPreference(Long preferenceId, UserAdPreferenceRequest request) {
		UserAdPreference preference = userAdPreferenceRepository.findById(preferenceId)
			.orElseThrow(() -> new IllegalArgumentException("선호도를 찾을 수 없습니다"));

		preference.updateInterest(request.getIsInterested());
		return AdvertisementConverter.toPreferenceResponse(preference);
	}

	@Transactional
	public void deleteUserPreference(Long preferenceId) {
		UserAdPreference preference = userAdPreferenceRepository.findById(preferenceId)
			.orElseThrow(() -> new IllegalArgumentException("선호도를 찾을 수 없습니다"));
		userAdPreferenceRepository.delete(preference);
	}

	public List<UserAdPreferenceResponse> getUserPreferences(Long userId) {
		return userAdPreferenceRepository.findByUserId(userId).stream()
			.map(AdvertisementConverter::toPreferenceResponse)
			.collect(Collectors.toList());
	}
}
