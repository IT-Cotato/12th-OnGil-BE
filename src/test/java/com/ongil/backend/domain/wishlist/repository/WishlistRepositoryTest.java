package com.ongil.backend.domain.wishlist.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.ongil.backend.domain.brand.entity.Brand;
import com.ongil.backend.domain.category.entity.Category;
import com.ongil.backend.domain.product.entity.Product;
import com.ongil.backend.domain.product.enums.ProductType;
import com.ongil.backend.domain.user.entity.User;
import com.ongil.backend.domain.wishlist.entity.Wishlist;

@DataJpaTest
@DisplayName("Wishlist Repository 테스트")
class WishlistRepositoryTest {

	@Autowired
	private WishlistRepository wishlistRepository;

	@Autowired
	private TestEntityManager em;

	private User user;
	private Product product;
	private Category category;
	private Brand brand;

	@BeforeEach
	void setUp() {
		// 카테고리 생성
		category = Category.builder()
			.name("테스트 카테고리")
			.displayOrder(1)
			.build();
		em.persist(category);

		// 브랜드 생성
		brand = Brand.builder()
			.name("테스트 브랜드")
			.description("테스트 브랜드 설명")
			.logoImageUrl("http://example.com/logo.png")
			.build();
		em.persist(brand);

		// 상품 생성
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
		em.persist(product);

		// 사용자 생성
		user = User.builder()
			.email("test@example.com")
			.name("테스트유저")
			.loginType(com.ongil.backend.domain.auth.entity.LoginType.GENERAL)
			.loginId("testuser")
			.build();
		em.persist(user);

		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("찜 목록 저장 테스트")
	void saveWishlist() {
		// given
		Wishlist wishlist = Wishlist.builder()
			.user(user)
			.product(product)
			.build();

		// when
		Wishlist savedWishlist = wishlistRepository.save(wishlist);
		em.flush();
		em.clear();

		// then
		Wishlist foundWishlist = wishlistRepository.findById(savedWishlist.getId()).orElse(null);
		assertThat(foundWishlist).isNotNull();
		assertThat(foundWishlist.getUser().getId()).isEqualTo(user.getId());
		assertThat(foundWishlist.getProduct().getId()).isEqualTo(product.getId());
	}

	@Test
	@DisplayName("중복 찜 확인 테스트")
	void existsByUserIdAndProductId() {
		// given
		Wishlist wishlist = Wishlist.builder()
			.user(user)
			.product(product)
			.build();
		wishlistRepository.save(wishlist);
		em.flush();

		// when
		boolean exists = wishlistRepository.existsByUserIdAndProductId(user.getId(), product.getId());

		// then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("존재하지 않는 찜 확인 테스트")
	void notExistsByUserIdAndProductId() {
		// when
		boolean exists = wishlistRepository.existsByUserIdAndProductId(user.getId(), product.getId());

		// then
		assertThat(exists).isFalse();
	}

	@Test
	@DisplayName("사용자별 찜 목록 조회 테스트")
	void findByUserIdOrderByCreatedAtDesc() {
		// given
		Wishlist wishlist1 = Wishlist.builder()
			.user(user)
			.product(product)
			.build();
		wishlistRepository.save(wishlist1);
		em.flush();

		// when
		List<Wishlist> wishlists = wishlistRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

		// then
		assertThat(wishlists).hasSize(1);
		assertThat(wishlists.get(0).getProduct().getName()).isEqualTo("테스트 상품");
		assertThat(wishlists.get(0).getProduct().getBrand()).isNotNull();
		assertThat(wishlists.get(0).getProduct().getCategory()).isNotNull();
	}

	@Test
	@DisplayName("사용자 + 카테고리별 찜 목록 조회 테스트")
	void findByUserIdAndCategoryWithProduct() {
		// given
		Wishlist wishlist = Wishlist.builder()
			.user(user)
			.product(product)
			.build();
		wishlistRepository.save(wishlist);
		em.flush();

		// when
		List<Wishlist> wishlists = wishlistRepository.findByUserIdAndCategoryWithProduct(
			user.getId(), category.getId());

		// then
		assertThat(wishlists).hasSize(1);
		assertThat(wishlists.get(0).getProduct().getCategory().getId()).isEqualTo(category.getId());
	}

	@Test
	@DisplayName("찜 삭제 테스트 - 성공")
	void deleteByIdAndUserId_Success() {
		// given
		Wishlist wishlist = Wishlist.builder()
			.user(user)
			.product(product)
			.build();
		Wishlist savedWishlist = wishlistRepository.save(wishlist);
		em.flush();
		em.clear();

		// when
		int deleted = wishlistRepository.deleteByIdAndUserId(savedWishlist.getId(), user.getId());
		em.flush();
		em.clear();

		// then
		assertThat(deleted).isEqualTo(1);
		assertThat(wishlistRepository.findById(savedWishlist.getId())).isEmpty();
	}

	@Test
	@DisplayName("찜 삭제 테스트 - 다른 사용자의 찜")
	void deleteByIdAndUserId_OtherUser() {
		// given
		User otherUser = User.builder()
			.email("other@example.com")
			.name("다른유저")
			.loginType(com.ongil.backend.domain.auth.entity.LoginType.GENERAL)
			.loginId("otheruser")
			.build();
		em.persist(otherUser);

		Wishlist wishlist = Wishlist.builder()
			.user(user)
			.product(product)
			.build();
		Wishlist savedWishlist = wishlistRepository.save(wishlist);
		em.flush();
		em.clear();

		// when
		int deleted = wishlistRepository.deleteByIdAndUserId(savedWishlist.getId(), otherUser.getId());
		em.flush();
		em.clear();

		// then
		assertThat(deleted).isEqualTo(0);
		assertThat(wishlistRepository.findById(savedWishlist.getId())).isPresent();
	}
}
