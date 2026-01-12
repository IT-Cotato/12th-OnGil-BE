package com.ongil.backend.domain.wishlist.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WishlistResponse {

	private Long wishlistId;
	private Long productId;
	private String productName;
	private String brandName;

	// 가격 정보
	private Integer originalPrice;
	private Integer discountRate;
	private Integer discountPrice;
	private Integer finalPrice;

	// 이미지
	private String thumbnailImageUrl;

	// 카테고리 정보 (상위 카테고리)
	private Long categoryId;
	private String categoryName;
}