package com.ongil.backend.domain.advertisement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ongil.backend.domain.advertisement.dto.request.AdvertisementCreateRequest;
import com.ongil.backend.domain.advertisement.dto.response.AdvertisementResponse;
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

@ExtendWith(MockitoExtension.class)
class AdvertisementServiceTest {

	@Mock
	private AdvertisementRepository advertisementRepository;

	@Mock
	private UserAdPreferenceRepository userAdPreferenceRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private BrandRepository brandRepository;

	@InjectMocks
	private AdvertisementService advertisementService;

	@Test
	@DisplayName("활성 광고 목록을 조회한다")
	void getActiveAdvertisements() {
		// given
		Advertisement ad1 = Advertisement.builder()
			.title("테스트 광고 1")
			.imageUrl("https://test1.com")
			.advertisementType(AdvertisementType.BANNER)
			.displayOrder(1)
			.startDate(LocalDateTime.now().minusDays(1))
			.endDate(LocalDateTime.now().plusDays(1))
			.isActive(true)
			.build();

		Advertisement ad2 = Advertisement.builder()
			.title("테스트 광고 2")
			.imageUrl("https://test2.com")
			.advertisementType(AdvertisementType.PROMOTION)
			.displayOrder(2)
			.startDate(LocalDateTime.now().minusDays(1))
			.endDate(LocalDateTime.now().plusDays(1))
			.isActive(true)
			.build();

		when(advertisementRepository.findActiveAdvertisements(any(LocalDateTime.class)))
			.thenReturn(Arrays.asList(ad1, ad2));

		// when
		List<AdvertisementResponse> result = advertisementService.getActiveAdvertisements();

		// then
		assertThat(result).hasSize(2);
		assertThat(result.get(0).getTitle()).isEqualTo("테스트 광고 1");
		assertThat(result.get(1).getTitle()).isEqualTo("테스트 광고 2");
	}

	@Test
	@DisplayName("사용자 맞춤 광고를 조회한다")
	void getPersonalizedAdvertisements() {
		// given
		Long userId = 1L;
		Category category = Category.builder()
			.name("테스트 카테고리")
			.displayOrder(1)
			.build();

		User user = User.builder()
			.email("test@test.com")
			.name("테스트")
			.build();

		UserAdPreference preference = UserAdPreference.builder()
			.user(user)
			.preferredCategory(category)
			.isInterested(true)
			.build();

		Advertisement ad = Advertisement.builder()
			.title("맞춤 광고")
			.imageUrl("https://test.com")
			.advertisementType(AdvertisementType.BANNER)
			.displayOrder(1)
			.targetCategory(category)
			.startDate(LocalDateTime.now().minusDays(1))
			.endDate(LocalDateTime.now().plusDays(1))
			.isActive(true)
			.build();

		when(userAdPreferenceRepository.findByUserIdAndIsInterestedTrue(userId))
			.thenReturn(Arrays.asList(preference));
		when(advertisementRepository.findActiveAdvertisementsByCategory(any(LocalDateTime.class), any()))
			.thenReturn(Arrays.asList(ad));

		// when
		List<AdvertisementResponse> result = advertisementService.getPersonalizedAdvertisements(userId);

		// then
		assertThat(result).isNotEmpty();
		assertThat(result.get(0).getTitle()).isEqualTo("맞춤 광고");
	}

	@Test
	@DisplayName("광고 노출 수를 증가시킨다")
	void incrementImpression() {
		// given
		Long advertisementId = 1L;
		Advertisement ad = Advertisement.builder()
			.title("테스트 광고")
			.imageUrl("https://test.com")
			.advertisementType(AdvertisementType.BANNER)
			.displayOrder(1)
			.startDate(LocalDateTime.now().minusDays(1))
			.endDate(LocalDateTime.now().plusDays(1))
			.isActive(true)
			.build();

		when(advertisementRepository.findById(advertisementId))
			.thenReturn(Optional.of(ad));

		// when
		advertisementService.incrementImpression(advertisementId);

		// then
		verify(advertisementRepository, times(1)).findById(advertisementId);
	}
}
