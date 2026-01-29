package com.ongil.backend.domain.wishlist.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ongil.backend.domain.brand.entity.Brand;
import com.ongil.backend.domain.category.entity.Category;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.enums.ProductType;
import com.ongil.backend.domain.product.repository.ProductRepository;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.user.repository.UserRepository;
import com.ongil.backend.domain.wishlist.converter.WishlistConverter;
import com.ongil.backend.domain.wishlist.dto.response.WishlistResponse;
import com.ongil.backend.domain.wishlist.entity.Wishlist;
import com.ongil.backend.domain.wishlist.repository.WishlistRepository;
import com.ongil.backend.global.common.exception.EntityNotFoundException;
import com.ongil.backend.global.common.exception.ErrorCode;
import com.ongil.backend.global.common.exception.ValidationException;

@ExtendWith(MockitoExtension.class)
@DisplayName("Wishlist Service 테스트")
class WishlistServiceTest {

	@Mock
	private WishlistRepository wishlistRepository;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private WishlistConverter wishlistConverter;

	@InjectMocks
	private WishlistService wishlistService;

	private User user;
	private Product product;
	private Wishlist wishlist;
	private WishlistResponse wishlistResponse;
	private Category category;
	private Brand brand;

	@BeforeEach
	void setUp() {
		// 카테고리 설정
		category = Category.builder()
			.name("테스트 카테고리")
			.displayOrder(1)
			.build();

		// 브랜드 설정
		brand = Brand.builder()
			.name("테스트 브랜드")
			.description("테스트 브랜드 설명")
			.logoImageUrl("http://example.com/logo.png")
			.build();

		// 사용자 설정
		user = User.builder()
			.id(1L)
			.email("test@example.com")
			.name("테스트유저")
			.loginType(com.ongil.backend.domain.auth.entity.LoginType.GENERAL)
			.loginId("testuser")
			.build();

		// 상품 설정
		product = Product.builder()
			.name("테스트 상품")
			.description("테스트 상품 설명")
			.price(10000)
			.materialOriginal("면 100%")
			.imageUrls("http://example.com/image1.jpg,http://example.com/image2.jpg")
			.sizes("S,M,L")
			.colors("Black,White")
			.discountRate(10)
			.discountPrice(9000)
			.productType(ProductType.NORMAL)
			.brand(brand)
			.category(category)
			.build();

		// 찜 설정
		wishlist = Wishlist.builder()
			.user(user)
			.product(product)
			.build();

		// 응답 DTO 설정
		wishlistResponse = WishlistResponse.builder()
			.wishlistId(1L)
			.productId(1L)
			.productName("테스트 상품")
			.brandName("테스트 브랜드")
			.price(10000)
			.discountRate(10)
			.finalPrice(9000)
			.thumbnailImageUrl("http://example.com/image1.jpg")
			.categoryId(1L)
			.categoryName("테스트 카테고리")
			.build();
	}

	@Test
	@DisplayName("찜 추가 성공")
	void addWishlist_Success() {
		// given
		Long userId = 1L;
		Long productId = 1L;

		given(userRepository.existsById(userId)).willReturn(true);
		given(productRepository.findWithBrandAndCategoryById(productId))
			.willReturn(Optional.of(product));
		given(wishlistRepository.existsByUserIdAndProductId(userId, productId))
			.willReturn(false);
		given(wishlistRepository.save(any(Wishlist.class))).willReturn(wishlist);
		given(wishlistConverter.toResponse(any(Wishlist.class))).willReturn(wishlistResponse);

		// when
		WishlistResponse result = wishlistService.addWishlist(userId, productId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getProductName()).isEqualTo("테스트 상품");
		verify(wishlistRepository, times(1)).save(any(Wishlist.class));
	}

	@Test
	@DisplayName("찜 추가 실패 - 사용자 없음")
	void addWishlist_UserNotFound() {
		// given
		Long userId = 999L;
		Long productId = 1L;

		given(userRepository.existsById(userId)).willReturn(false);

		// when & then
		assertThatThrownBy(() -> wishlistService.addWishlist(userId, productId))
			.isInstanceOf(EntityNotFoundException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);
	}

	@Test
	@DisplayName("찜 추가 실패 - 상품 없음")
	void addWishlist_ProductNotFound() {
		// given
		Long userId = 1L;
		Long productId = 999L;

		given(userRepository.existsById(userId)).willReturn(true);
		given(productRepository.findWithBrandAndCategoryById(productId))
			.willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> wishlistService.addWishlist(userId, productId))
			.isInstanceOf(EntityNotFoundException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.PRODUCT_NOT_FOUND);
	}

	@Test
	@DisplayName("찜 추가 실패 - 이미 찜한 상품")
	void addWishlist_AlreadyExists() {
		// given
		Long userId = 1L;
		Long productId = 1L;

		given(userRepository.existsById(userId)).willReturn(true);
		given(productRepository.findWithBrandAndCategoryById(productId))
			.willReturn(Optional.of(product));
		given(wishlistRepository.existsByUserIdAndProductId(userId, productId))
			.willReturn(true);

		// when & then
		assertThatThrownBy(() -> wishlistService.addWishlist(userId, productId))
			.isInstanceOf(ValidationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.WISHLIST_ALREADY_EXISTS);
	}

	@Test
	@DisplayName("찜 삭제 성공")
	void removeWishlist_Success() {
		// given
		Long userId = 1L;
		Long wishlistId = 1L;

		given(wishlistRepository.deleteByIdAndUserId(wishlistId, userId)).willReturn(1);

		// when
		wishlistService.removeWishlist(userId, wishlistId);

		// then
		verify(wishlistRepository, times(1)).deleteByIdAndUserId(wishlistId, userId);
	}

	@Test
	@DisplayName("찜 삭제 실패 - 찜 없음")
	void removeWishlist_NotFound() {
		// given
		Long userId = 1L;
		Long wishlistId = 999L;

		given(wishlistRepository.deleteByIdAndUserId(wishlistId, userId)).willReturn(0);

		// when & then
		assertThatThrownBy(() -> wishlistService.removeWishlist(userId, wishlistId))
			.isInstanceOf(EntityNotFoundException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.WISHLIST_NOT_FOUND);
	}

	@Test
	@DisplayName("내 찜 목록 조회 - 전체")
	void getMyWishlists_All() {
		// given
		Long userId = 1L;
		List<Wishlist> wishlists = Arrays.asList(wishlist);

		given(wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId))
			.willReturn(wishlists);
		given(wishlistConverter.toResponseList(wishlists))
			.willReturn(Arrays.asList(wishlistResponse));

		// when
		List<WishlistResponse> result = wishlistService.getMyWishlists(userId, null);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getProductName()).isEqualTo("테스트 상품");
		verify(wishlistRepository, times(1)).findByUserIdOrderByCreatedAtDesc(userId);
	}

	@Test
	@DisplayName("내 찜 목록 조회 - 카테고리 필터링")
	void getMyWishlists_WithCategoryFilter() {
		// given
		Long userId = 1L;
		Long categoryId = 1L;
		List<Wishlist> wishlists = Arrays.asList(wishlist);

		given(wishlistRepository.findByUserIdAndCategoryWithProduct(userId, categoryId))
			.willReturn(wishlists);
		given(wishlistConverter.toResponseList(wishlists))
			.willReturn(Arrays.asList(wishlistResponse));

		// when
		List<WishlistResponse> result = wishlistService.getMyWishlists(userId, categoryId);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.get(0).getCategoryName()).isEqualTo("테스트 카테고리");
		verify(wishlistRepository, times(1))
			.findByUserIdAndCategoryWithProduct(userId, categoryId);
	}

	@Test
	@DisplayName("내 찜 목록 조회 - 빈 목록")
	void getMyWishlists_Empty() {
		// given
		Long userId = 1L;

		given(wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId))
			.willReturn(Arrays.asList());
		given(wishlistConverter.toResponseList(Arrays.asList()))
			.willReturn(Arrays.asList());

		// when
		List<WishlistResponse> result = wishlistService.getMyWishlists(userId, null);

		// then
		assertThat(result).isEmpty();
	}
}
